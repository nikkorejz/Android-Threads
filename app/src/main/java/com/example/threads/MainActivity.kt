package com.example.threads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.Observable
import java.util.Observer
import kotlin.concurrent.thread

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1.
//        val thread = Thread {
//            Thread.sleep(2000)
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }
//        // Программа способна завершиться не дожидаясь завершения демонов
//        thread.isDaemon = true
//        thread.start()

        // 2.
//        val thread = object: Thread() {
//            override fun run() {
//                Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//            }
//        }
//        thread.start()

        // 3. Не нужно вызывать start()
        // Или мы можем вызвать run, перепутав его со start
//        val thread = thread {
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }

        // 4. Остановка потока
//        val thread = thread {
//            while (true) {
//                // ..
//            }
//        }

        // Если в другом потоке вызвать interrupt, то поток не завершится,
        // поскольку мы указываем намерение его завершить, а не убиваем его
//        thread.interrupt()
        // Добавить Thread.sleep(1) неэффективно
//        Don't use it.
//        thread.stop()

        // 5. sleep бросает InterruptException
//        val thread = thread {
//            Thread.sleep(2000)
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }
        // Сразу завершаем поток
        // sleep кинет исключение и поток завершится
//        thread.interrupt()

        // 6. Как лучше сделать
//        val thread = thread {
//            while (true) {
//                if (!Thread.interrupted()) {
//                    // Из-за большого кол-ва сообщений 2 секунды растянутся на 20
//                    // Log.i(TAG, "Hello")
//                } else {
//                    Log.i(TAG, "Thread interrupted")
//                    break
//                }
//            }
//        }
//
//        val thread2 = thread {
//            Thread.sleep(2000)
//            thread.interrupt()
//        }

        // 7. Как ждать поток
        // join блокирует поток, в котором он был вызван, пока не завершится другой поток
//        val thread = thread {
//            Thread.sleep(2000)
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }
//        thread.join()
//        Log.i(TAG, "Hello from: main")

        // 8. 2 потока
//        val thread = thread {
//            Thread.sleep(2000)
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }
//
//        val thread2 = thread(name = "Thread 22") {
//            Thread.sleep(1000)
//            Log.i(TAG, "Hello from: ${Thread.currentThread().name}")
//        }
//        thread.join()
//        thread2.join()
//        Log.i(TAG, "Hello from: main")

        // 9.
//        var a = 0
//        var b = 0
//
//        val thread = thread {
//            a = 1
//            Log.i(TAG, b.toString())
//        }
//
//        val thread2 = thread {
//            b = 1
//            Log.i(TAG, a.toString())
//        }
//
//        thread.join()
//        thread2.join()

        // Вероятно получим 0 1

        // Можем получить 1 1 (Race Condition).
        // Так как потоки могут запуститься одновременно, то и результат может быть разным от случая к случаю

        // 0 1 или 0 0
        // Если 2 или более потока имеют доступ к 1 ресурсу (без объявления, что ресурс потоко-безопасный)
        // Компилятор может применить свои внутренние оптимизации и, например, поменять строчки местами в каких-то узких местах
        // Например в теле потока 1 если мы сначала напечатаем a, а потом изменим b, то в данной области логика не будет нарушена
        // Если не используем потоко-безопасные конструкции, то нет гарантий, что данные актуальны
        // Каждый поток имеет свой кэш, и работает с ним.
        // Для решения этой проблемы требуется использовать безопасный доступ к ресурсу
        // Доступ может быть блокирующий или неблокирующий
        // mutex (mutual exclusion) - спец. объект для синхронизации потоков
        // Аналогия: комната с замком. После того, как поток "выйдет из комнаты", он передаст mutex (ключ) другому потоку.
        // Блокировка должна быть одним и тем же mutex'ом
        // Блокировать нужно как чтение так и запись
        // https://stackoverflow.com/questions/71924456/can-i-use-getapplicationcontext-object-for-synchronizing

//        val lock = Any()
//        synchronized(lock) {
//
//        }

        // 10. mutex
//        var a = 0
//        var b = 0
//        val mutex = Any()
//        val thread = thread {
//            synchronized(mutex) {
//                a = 1
//                Log.i(TAG, b.toString())
//            }
//        }
//
//        val thread2 = thread {
//            synchronized(mutex) {
//                b = 1
//                Log.i(TAG, a.toString())
//            }
//        }
//
//        thread.join()
//        thread2.join()
        // Теперь мы получим всегда 0 1
    }

    // 11. Пример с registerActivityLifecycleCallbacks в android.app.Activity
    interface MyObserver {
        fun onUpdate()
    }

    interface MyObservable {
        fun register(value: MyObserver)
        fun unregister(value: MyObserver)
        fun notifyObservers()
    }

    // Вариант для одного потока
//    class MyClass : MyObservable {
//
//        private val subs: MutableSet<MyObserver> = mutableSetOf()
//
//        override fun register(value: MyObserver) {
//            subs.add(value)
//        }
//
//        override fun unregister(value: MyObserver) {
//            subs.remove(value)
//        }
//
//        override fun notifyObservers() {
//            // Казалось бы, просто читаем,
//            // но в этот момент может прилететь запрос на добавление/удаление
//            subs.forEach { it.onUpdate() }
//        }
//
//    }

    // Вариант для мультипотока
    class MyClass : MyObservable {

        private val mutex = Any()
        private val subs: MutableSet<MyObserver> = mutableSetOf()

        override fun register(value: MyObserver) {
            synchronized(mutex) {
                subs.add(value)
            }
        }

        override fun unregister(value: MyObserver) {
            synchronized(mutex) {
                subs.remove(value)
            }
        }

        override fun notifyObservers() {
            // Мы не знаем реализацию onUpdate каждого подписчика
            // Мы можем попасть в а-ля dead-lock, заблокировав другие операции если подписчик очень долго работает
//            synchronized(mutex) {
//                subs.forEach { it.onUpdate() }
//            }

            // Решение: создаем копию массив
            // Так как проходим по копии, то нам неважно изменился оригинальный объект или нет
            val copy = synchronized(mutex) {
                subs.toList()
            }
            copy.forEach { it.onUpdate() }
        }

    }
}