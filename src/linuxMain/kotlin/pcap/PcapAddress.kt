package pcap

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.linux.sockaddr_ll
import platform.posix.AF_INET
import platform.posix.AF_INET6
import platform.posix.AF_PACKET

actual fun PcapAddress(pointer: CPointer<sockaddr>): PcapAddress {
    return when (pointer.pointed.sa_family.toInt()) {
        AF_INET -> {
            val data = pointer.readBytes(4)
            PcapAddress(PcapAddress.AddressFamily.INET4, data)
        }

        AF_INET6 -> {
            val data = pointer.readBytes(16)
            PcapAddress(PcapAddress.AddressFamily.INET6, data)
        }

        AF_PACKET -> {
            val mac = pointer.reinterpret<sockaddr_ll>().pointed
            val len = mac.sll_halen.toInt()
            val data = mac.sll_addr.readBytes(len)
            PcapAddress(PcapAddress.AddressFamily.MAC, data)
        }

        else -> throw UnsupportedOperationException(pointer.pointed.sa_family.toString())
    }
}
