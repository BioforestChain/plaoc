// import bigInt from 'big-integer';
// http://peterolson.github.io/BigInteger.js/

// https://blog.csdn.net/xiaopeng9275/article/details/72123709
// https://zhuanlan.zhihu.com/p/36330307
// https://segmentfault.com/a/1190000011282426

/**
 * Twitter_Snowflake
 *
 * SnowFlake的结构如下(共64bits，每部分用-分开):
 *   0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 *   |   ----------------------|----------------------   --|--   --|--   -----|------
 * 1bit不用                41bit 时间戳                  数据标识id 机器id     序列号id
 *
 * - 1位标识，二进制中最高位为1的都是负数，但是我们生成的id一般都使用整数，所以这个最高位固定是0
 * - 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * - 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
 * - 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 * - 加起来刚好64位，为一个Long型。
 * SnowFlake的优点是
 *   - 整体上按照时间自增排序
 *   - 并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)
 *   - 并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
export default class SnowFlake {
  // 开始时间截 (2022-10-12)，这个可以设置开始使用该系统的时间，可往后使用69年
  private readonly twepoch = 1665558753715n;

  // 位数划分 [数据标识id(5bit 31)、机器id(5bit 31)](合计共支持1024个节点)、序列id(12bit 4095)
  private readonly workerIdBits: bigint = 5n; // 标识ID
  private readonly dataCenterIdBits: bigint = 5n; // 机器ID
  private readonly sequenceBits: bigint = 12n; // 序列ID

  // 支持的最大十进制id
  // 这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数
  // -1 左移5位后与 -1 异或
  private readonly maxWorkerId: bigint = -1n ^ (-1n << this.workerIdBits);
  private readonly maxDataCenterId: bigint =
    -1n ^ (-1n << this.dataCenterIdBits);
  // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
  private readonly sequenceMask: bigint = -1n ^ (-1n << this.sequenceBits);

  // 机器ID向左移12位 数据标识id向左移17位(12+5) 时间截向左移22位(5+5+12)
  private readonly workerIdShift: bigint = this.sequenceBits;
  private readonly dataCenterIdShift: bigint =
    this.sequenceBits + this.workerIdBits;
  private readonly timestampLeftShift: bigint =
    this.dataCenterIdShift + this.dataCenterIdBits;

  // 工作机器ID(0~31) 数据中心ID(0~31) 毫秒内序列(0~4095)
  private sequence: bigint = 0n;
  // 上次生成ID的时间截（这个是在内存中？系统时钟回退+重启后呢）
  private lastTimestamp: bigint = -1n;

  private readonly workerId: bigint;
  private readonly dataCenterId: bigint;
  constructor(workerId: bigint, dataCenterId: bigint) {
    if (workerId > this.maxWorkerId || workerId < 0n) {
      throw new Error(
        `workerId can't be greater than ${this.maxWorkerId} or less than 0`
      );
    }
    if (dataCenterId > this.maxDataCenterId || dataCenterId < 0n) {
      throw new Error(
        `dataCenterId can't be greater than ${this.maxDataCenterId} or less than 0`
      );
    }
    this.workerId = workerId;
    this.dataCenterId = dataCenterId;
    return this;
  }

  /**
   * 获得下一个ID (该方法是线程安全的)
   *
   * @returns {bigint} SnowflakeId 返回 id
   */
  public nextId(): bigint {
    let timestamp = this.currentLinuxTime();
    // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
    const diff = timestamp - this.lastTimestamp;

    if (diff < 0n) {
      throw new Error(
        `Clock moved backwards. Refusing to generate id for ${-diff} milliseconds`
      );
    }

    // 如果是同一时间生成的，则进行毫秒内序列
    if (diff === 0n) {
      this.sequence = (this.sequence + 1n) & this.sequenceMask;
      // 毫秒内序列溢出
      if (this.sequence == 0n) {
        // 阻塞到下一个毫秒，获得新的时间戳
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      // 时间戳改变，毫秒内序列重置
      this.sequence = 0n;
    }

    // 保存上次生成ID的时间截
    this.lastTimestamp = timestamp;

    // 移位并通过或运算拼到一起组成64位的ID
    // 将各 bits 位数据移位后或运算合成一个大的64位二进制数据
    return (
      ((timestamp - this.twepoch) << this.timestampLeftShift) |
      (this.dataCenterId << this.dataCenterIdShift) |
      (this.workerId << this.workerIdShift) |
      this.sequence
    );
  }

  /**
   * 阻塞到下一个毫秒，直到获得新的时间戳
   * @param {bigint} lastTimestamp 上次生成ID的时间截
   * @return {bigint} 当前时间戳
   */
  private tilNextMillis(lastTimeStamp: bigint) {
    let timestamp: bigint = this.currentLinuxTime();
    while (timestamp <= lastTimeStamp) {
      timestamp = this.currentLinuxTime();
    }
    return timestamp;
  }

  /**
   * 返回以毫秒为单位的当前时间
   * @return {bigint} 当前时间(毫秒)
   */
  private currentLinuxTime(): bigint {
    return BigInt(new Date().valueOf());
  }
}
