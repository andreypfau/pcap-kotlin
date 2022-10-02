package pcap

import kotlinx.cinterop.*

actual object Pcap {
    actual val libVersion: String
//        get() = requireNotNull(pcap_lib_version()).toKString()
        get() = TODO()

    actual fun lookupDev(): PcapNetworkInterface? = memScoped {
//        val buf = alloc<ByteVar>()
//        val name = pcap_lookupdev(buf.ptr)?.toKString()
//        findAllDevs().find { it.name == name }
        TODO()
    }

    actual fun findAllDevs(): List<PcapNetworkInterface> = memScoped {
        TODO()
//        val alldevs = alloc<CPointerVar<pcap_if_t>>()
//        val errbuf = errbuf()
//        val rc = pcap_findalldevs(alldevs.ptr, errbuf)
//        require(rc == 0) { "$rc ${errbuf.toKString()}" }
//        val networkInterfaces = ArrayList<PcapNetworkInterface>()
//        var dev: pcap_if? = alldevs.pointed
//        while (dev != null) {
//            val addresses = pcapAddresses(dev.addresses)
//            val networkInterface = PcapNetworkInterface(
//                dev.name?.toKString() ?: "",
//                dev.description?.toKString() ?: "",
//                addresses,
//            )
//            networkInterfaces.add(networkInterface)
//            dev = dev.next?.pointed
//        }
//        networkInterfaces
    }
}
