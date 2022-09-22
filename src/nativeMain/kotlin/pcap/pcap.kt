package pcap

import kotlinx.cinterop.*

object Pcap {
    val libVersion: String
        get() = requireNotNull(pcap_lib_version()) {
            "pcap not found"
        }.toKString()

    fun lookupDev(): PcapNetworkInterface? = memScoped {
        val buf = alloc<ByteVar>()
        val name = pcap_lookupdev(buf.ptr)?.toKString()
        findAllDevs().find { it.name == name }
    }

    fun findAllDevs(): List<PcapNetworkInterface> = memScoped {
        val alldevs = alloc<CPointerVar<pcap_if_t>>()
        val errbuf = errbuf()
        val rc = pcap_findalldevs(alldevs.ptr, errbuf)
        require(rc == 0) { "$rc ${errbuf.toKString()}" }
        val networkInterfaces = ArrayList<PcapNetworkInterface>()
        var dev: pcap_if? = alldevs.pointed
        while (dev != null) {
            val addresses = PcapAddress.newInstances(dev.addresses)
            val networkInterface = PcapNetworkInterface(
                dev.name?.toKString() ?: "",
                dev.description?.toKString() ?: "",
                addresses,
            )
            networkInterfaces.add(networkInterface)
            dev = dev.next?.pointed
        }
        networkInterfaces
    }
}

internal fun MemScope.errbuf() = allocArray<ByteVar>(256)
internal fun ByteArray.hex(separator: String = "") =
    joinToString(separator) { it.toUByte().toString(16).padStart(2, '0') }
