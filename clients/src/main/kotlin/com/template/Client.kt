package com.template

import kr.co.busanbank.bmt.flow.SetDigitalFlow
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.NetworkHostAndPort.Companion.parse
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.LocalTime

/**
 * Connects to a Corda node via RPC and performs RPC operations on the node.
 *
 * The RPC connection is configured using command line arguments.
 */
//fun main(args: Array<String>) = Client().main(args)
//
//private class Client {
//    companion object {
//        val logger = loggerFor<Client>()
//    }
//
//    fun main(args: Array<String>) {
//        // Create an RPC connection to the node.
//        require(args.size == 3) { "Usage: Client <node address> <rpc username> <rpc password>" }
//        val nodeAddress = parse(args[0])
//        val rpcUsername = args[1]
//        val rpcPassword = args[2]
//        val client = CordaRPCClient(nodeAddress)
//        val proxy = client.start(rpcUsername, rpcPassword).proxy
//
//        // Interact with the node.
//        // For example, here we print the nodes on the network.
//        val nodes = proxy.networkMapSnapshot()
//        logger.info("{}", nodes)
//    }
//}

fun main(args: Array<String>) = Client().main(args)

private class Client {
    companion object {
        val logger = loggerFor<Client>()
    }

    class SimpleThread(val args: Array<String>): Thread() {

        public override fun run() {

            val fileName = "C:\\Users\\john.paual\\Desktop\\test\\cordapp-template-kotlin\\clients\\src\\main\\kotlin\\com\\template\\logs.txt"
            val file: File = File(fileName)
            Files.newBufferedWriter(file.toPath())
            val content = "${Thread.currentThread()} has started.\n"
            println(content)
            logger.info(content)
            Files.write(file.toPath(), content.toByteArray(), StandardOpenOption.APPEND)

            val nodeAddress = parse("localhost:20006")
            val rpcUsername = "user1"
            val rpcPassword = "test"
//            val nodeAddress = parse(args[6])
//            val rpcUsername = args[7]
//            val rpcPassword = args[8]
            val client = CordaRPCClient(nodeAddress)
            val proxy = client.start(rpcUsername, rpcPassword).proxy

//            val recipient = proxy.partiesFromName("O=C4009730c-298d-49ba-bc61-de2af4c5fbe0, OU=C4009730c-298d-49ba-bc61-de2af4c5fbe0, L=London, C=GB", false)
//            val recipient: Party? = proxy.wellKnownPartyFromX500Name(CordaX500Name(commonName = null, state = null,organisationUnit = args[2],organisation =  args[3], locality = args[4],country =  args[5]))
            val recipient: Party? = proxy.wellKnownPartyFromX500Name(CordaX500Name(commonName = null, state = null,organisationUnit = null, organisation =  "Issuer1", locality = "HK",country =  "HK"))
//            val observer = proxy.partiesFromName("Citi Bank", false)
            val party = mutableListOf<Party>()
            party.add(recipient!!)


            //******* modify numberOfTxn here********
//            val numberOfTxn = args[1].toInt()
            val numberOfTxn = 100
            for (y in 1..numberOfTxn) {
                val result = proxy.startFlowDynamic(
                        SetDigitalFlow::class.java,
                        party,
                        "123",
                        "123",
                        "Erwin",
                        "1000",
                        "50",
                        "25",
                        "test",
                        "PH"
                ).returnValue.getOrThrow()

//                logger.info(result.toString())
                val content1 = "${LocalTime.now()} ${Thread.currentThread()} - txn#${y} is done -> ${result.toString()}\n"
                println(content1)
                Files.write(file.toPath(), content1.toByteArray(), StandardOpenOption.APPEND)
            }


        }
    }

    fun main(args: Array<String>) {
        //******* modify numberOfThread here (it is the number of thread or connection to exchange node) ********
        //increase this number until the exchange node is CPU fully utilized
        //i think it should be equal to number of CPU core of the node
        //remember set rpcThreadPoolSize and flowThreadPoolSize in node configuration

//        val numberOfThread = args[0].toInt()
        val numberOfThread = 5
        for (x in 1..numberOfThread) {

            val thread = SimpleThread(args)
            thread.start()
        }
    }
}