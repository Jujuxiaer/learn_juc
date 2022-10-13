package com.jujuxiaer.juc.interrupt;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @auther zzyy
 * @create 2021-03-03 18:20
 */
public class InterruptDemo {
	static volatile boolean isStop = false;
	static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

	/**
	 * 通过一个volatile变量实现线程的等待唤醒
	 */
	@Test
	public void test01() {
		m1();
	}

	/**
	 * 通过AtomicBoolean实现线程的等待唤醒
	 */
	@Test
	public void test02() {
		m2();
	}

	/**
	 * 通过Thread类自带的中断api方法 t1.interrupt()
	 * 实现线程的等待唤醒
	 */
	@Test
	public void test03() {
		m3();
	}

	/**
	 * 使用t1.interrupt()只是将t1线程中断标识设置成true，如果t1线程自己不配合，也起不到中断的作用。
	 * 中断只是协商，中断协商机制
	 * 中断为true后，并不是立刻stop程序
	 */
	@Test
	public void test04() {
		m4();
	}

	/**
	 *
	 */
	@Test
	public void test05() {
		m5();
	}

	/**
	 * 验证 Thread.interrupted() 清除中断标识
	 */
	@Test
	public void test06() {
		m6();
	}


	/**
	 * 验证 Thread.interrupted() 清除中断标识
	 */
	public void m6() {
		System.out.println(Thread.currentThread().getName() + "---" + Thread.interrupted());
		System.out.println(Thread.currentThread().getName() + "---" + Thread.interrupted());
		System.out.println("111111");
		Thread.currentThread().interrupt();///----false---> true
		System.out.println("222222");
		System.out.println(Thread.currentThread().getName() + "---" + Thread.interrupted());
		System.out.println(Thread.currentThread().getName() + "---" + Thread.interrupted());
	}

	/**
	 * sleep方法抛出InterruptedException后，中断标识也被清空重置为false，
	 * 我们如果在catch语句中没有通过thread.interrupt()方法再次将中断标识改成true，就会导致了无限循环。
	 */
	public void m5() {
		Thread t1 = new Thread(() -> {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("-----isInterrupted() = true，程序结束。");
					break;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// Thread.currentThread().interrupt();//线程的中断标志位为false,无法停下，需要再次掉interrupt()设置true
					e.printStackTrace();
				}
				System.out.println("------hello Interrupt");
			}
		}, "t1");
		t1.start();

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			t1.interrupt();// 修改t1线程的中断标志位为true
		}, "t2").start();
	}

	/**
	 * 使用t1.interrupt()只是将t1线程中断标识设置成true，如果t1线程自己不配合，也起不到中断的作用。
	 * 中断只是协商，中断协商机制
	 * 中断为true后，并不是立刻stop程序
	 */
	public void m4() {
		// 中断为true后，并不是立刻stop程序
		Thread t1 = new Thread(() -> {
			for (int i = 1; i <= 300; i++) {
				System.out.println("------i: " + i);
			}
			System.out.println("t1.interrupt()调用之后02： " + Thread.currentThread().isInterrupted());
		}, "t1");
		t1.start();

		System.out.println("t1.interrupt()调用之前,t1线程的中断标识默认值： " + t1.isInterrupted());
		// 实例方法interrupt()仅仅是设置线程的中断状态位设置为true，不会停止线程
		t1.interrupt();
		// 活动状态,t1线程还在执行中
		System.out.println("t1.interrupt()调用之后01： " + t1.isInterrupted());

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 睡3秒后,t1线程执行完了，不在执行中了，已经结束执行了。
		System.out.println("t1.interrupt()调用之后03： " + t1.isInterrupted());
	}

	public void m3() {
		Thread t1 = new Thread(() -> {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("-----isInterrupted() = true，程序结束。");
					break;
				}
				System.out.println("------hello Interrupt");
			}
		}, "t1");
		t1.start();

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			t1.interrupt();// 修改t1线程的中断标志位为true
		}, "t2").start();
	}

	/**
	 * 通过AtomicBoolean
	 */
	public void m2() {
		new Thread(() -> {
			while (true) {
				if (atomicBoolean.get()) {
					System.out.println("-----atomicBoolean.get() = true，程序结束。");
					break;
				}
				System.out.println("------hello atomicBoolean");
			}
		}, "t1").start();

		// 暂停几秒钟线程
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			atomicBoolean.set(true);
		}, "t2").start();
	}

	/**
	 * 通过一个volatile变量实现
	 */
	public void m1() {
		new Thread(() -> {
			while (true) {
				if (isStop) {
					System.out.println("-----isStop = true，程序结束。");
					break;
				}
				System.out.println("------hello isStop");
			}
		}, "t1").start();

		// 暂停几秒钟线程
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			isStop = true;
		}, "t2").start();
	}
}
