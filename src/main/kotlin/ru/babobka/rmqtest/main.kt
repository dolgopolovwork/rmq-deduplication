package ru.babobka.rmqtest

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Delivery
import com.rabbitmq.client.MessageProperties

fun main() {

    val queueName = "test123"
    val connectionFactory = ConnectionFactory()
    connectionFactory.host = "localhost"
    connectionFactory.port = 5672

    val connection = connectionFactory.newConnection()
    val channel = connection.createChannel()
    val arguments = hashMapOf<String, Any>(
        Pair("x-message-deduplication", true),
        Pair("x-cache-persistence", "disk"),
        Pair("x-cache-ttl", 60_000 * 60 * 24)
    )
    channel.queueDeclare(queueName, true, false, false, arguments)
    Thread(Runnable {
        while (true) {
            Thread.sleep(5_000)
            println("Publish")
            val params =
                MessageProperties.MINIMAL_PERSISTENT_BASIC.builder()
                    .headers(
                        hashMapOf(Pair<String, Any>("x-deduplication-header", "12346"))
                    ).build()
            channel.basicPublish(
                "",
                queueName,
                params,
                "kek ${System.currentTimeMillis()}".toByteArray()
            )
        }
    }).start()
    val deliveryCallback = { _: String, delivery: Delivery ->
        println("Got message ${String(delivery.body)}")
    }
    channel.basicConsume(queueName, true, deliveryCallback, { _ -> })
}