package pcap

import kotlinx.cinterop.*
import platform.linux.sockaddr_ll
import platform.posix.*

actual fun PcapAddress(pointer: CPointer<sockaddr>): PcapAddress {
    return when (pointer.pointed.sa_family.toInt()) {
        AF_INET -> {
            val inet4 = pointer.reinterpret<sockaddr_in>().pointed
            val data = memScoped {
                val bytes = allocArray<ByteVar>(4)
                memcpy(bytes, inet4.sin_addr.ptr, 4)
                bytes.readBytes(4)
            }
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
