# 时间轮

## 1.存在原因

当存在存在大量的延时任务的时候，使用DelayQueue（O(nlog(n)) ）实现，时间复杂度是比较高的。 因为DelayQueue是通过优先级队列实现。 优先级队列是通过 堆 进行排序的，当流量大时，需要进行大量的旋转操作而耗费时间。

时间轮（TimingWheel）是一个存储定时任务的环形队列，数组中的每个元素可以存放一个定时任务列表，其中存放了真正的定时任务。这样 不管流量多大，DelayQueue中存放的都是一个环形队列中的元素。经过简单测试，当流量小时，使用DelayQueue效率更高。当流量大事，使用时间轮将大大提高效率。

## 2.基本原理

![1616238063733](https://gitee.com/whiteandy/TimeWheel/blob/master/img/1616238058.png)



​	时间轮由多个时间格组成，每个时间格代表当前时间轮的基本时间跨度（tickMs）。时间轮的时间格个数是固定的，可用wheelSize来表示，那么整个时间轮的总体时间跨度（interval）可以通过公式 tickMs × wheelSize计算得出。时间轮还有一个表盘指针（currentTime），用来表示时间轮当前所处的时间，currentTime是tickMs的整数倍。currentTime可以将整个时间轮划分为到期部分和未到期部分，currentTime当前指向的时间格也属于到期部分，表示刚好到期，需要处理此时间格所对应的任务列表中的所有任务。

​	若时间轮的tickMs=1ms，wheelSize=20，那么可以计算得出interval为20ms。初始情况下表盘指针currentTime指向时间格0，此时有一个定时为2ms的任务插入进来会存放到时间格为2的任务列表中。随着时间的不断推移，指针currentTime不断向前推进，过了2ms之后，当到达时间格2时，就需要将时间格2所对应的任务列表中的任务做相应的到期操作。此时若又有一个定时为8ms的任务插入进来，则会存放到时间格10中，currentTime再过8ms后会指向时间格10。如果同时有一个定时为19ms的任务插入进来怎么办？新来的定时任务会复用原来的定时任务列表，所以它会插入到原本已经到期的时间格1中。总之，整个时间轮的总体跨度是不变的，随着指针currentTime的不断推进，当前时间轮所能处理的时间段也在不断后移，总体时间范围在currentTime和currentTime+interval之间。

​	如果此时有个定时为350ms的任务该如何处理？直接扩充wheelSize的大小么？如果有几万甚至几十万的定时任务呢，这个wheelSize的扩充没有底线，为此引入了层级时间轮的概念，当任务的到期时间超过了当前时间轮所表示的时间范围时，就会尝试添加到上层时间轮中。

![1616238366(1)](.\img\1616238366.png)

参考上图，复用之前的案例，第一层的时间轮tickMs=1ms, wheelSize=20, interval=20ms。第二层的时间轮的tickMs为第一层时间轮的interval，即为20ms。每一层时间轮的wheelSize是固定的，都是20，那么第二层的时间轮的总体时间跨度interval为400ms。以此类推，这个400ms也是第三层的tickMs的大小，第三层的时间轮的总体时间跨度为8000ms。

对于之前所说的350ms的定时任务，显然第一层时间轮不能满足条件，所以就升级到第二层时间轮中，最终被插入到第二层时间轮中时间格17所对应的TimerTaskList中。如果此时又有一个定时为450ms的任务，那么显然第二层时间轮也无法满足条件，所以又升级到第三层时间轮中，最终被插入到第三层时间轮中时间格1的TimerTaskList中。注意到在到期时间在[400ms,800ms)区间的多个任务（比如446ms、455ms以及473ms的定时任务）都会被放入到第三层时间轮的时间格1中，时间格1对应的TimerTaskList的超时时间为400ms。随着时间的流逝，当次TimerTaskList到期之时，原本定时为450ms的任务还剩下50ms的时间，还不能执行这个任务的到期操作。这里就有一个时间轮降级的操作，会将这个剩余时间为50ms的定时任务重新提交到层级时间轮中，此时第一层时间轮的总体时间跨度不够，而第二层足够，所以该任务被放到第二层时间轮到期时间为[40ms,60ms)的时间格中。再经历了40ms之后，此时这个任务又被“察觉”到，不过还剩余10ms，还是不能立即执行到期操作。所以还要再有一次时间轮的降级，此任务被添加到第一层时间轮到期时间为[10ms,11ms)的时间格中，之后再经历10ms后，此任务真正到期，最终执行相应的到期操作。
