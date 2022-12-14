package pcap

import kotlinx.cinterop.*
import platform.posix.*

internal fun pcapAddresses(pointer: CPointer<pcap_addr>?): List<PcapAddress> {
    if (pointer == null) return emptyList()
    val addresses = ArrayList<PcapAddress>()
    var pcapAddr: pcap_addr? = pointer.pointed
    while (pcapAddr != null) {
        val addrPointer = pcapAddr.addr
        if (addrPointer != null) {
            val pcapAddress = fromSockAddr(addrPointer)
            if (pcapAddress != null) {
                addresses.add(pcapAddress)
            }
        }
        pcapAddr = pcapAddr.next?.pointed
    }
    return addresses
}

private fun fromSockAddr(pointer: CPointer<sockaddr>): PcapAddress? {
    return when (pointer.pointed.sa_family.toInt()) {
        AF_INET -> {
            val inet4 = pointer.reinterpret<sockaddr_in>().pointed
            val data = memScoped {
                val bytes = allocArray<ByteVar>(4)
                memcpy(bytes, inet4.sin_addr.ptr, 4)
                bytes.readBytes(4)
            }
            PcapAddress(PcapAddressFamily.INET4, data)
        }

        AF_INET6 -> {
            val data = pointer.readBytes(16)
            PcapAddress(PcapAddressFamily.INET6, data)
        }

        else -> throw UnsupportedOperationException(pointer.pointed.sa_family.toString())
    }
}
