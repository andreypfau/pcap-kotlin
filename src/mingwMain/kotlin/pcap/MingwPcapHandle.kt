package pcap

import kotlinx.cinterop.*
import platform.posix.memcpy

actual class PcapHandle(
    val handle: CPointer<pcap_t>,
) {
    actual fun nextPacket(rawData: ByteArray, offset: Int, length: Int): Int = memScoped {
        val header = alloc<pcap_pkthdr>()
        val packet = pcap_next(handle, header.ptr)
        if (packet != null) {
            rawData.usePinned {
                it.addressOf(offset)
                val len = minOf(header.caplen, length.toUInt())
                memcpy(it.addressOf(offset), packet, len.convert())
                len.toInt()
            }
        } else {
            0
        }
    }

    actual fun sendPacket(rawData: ByteArray, offset: Int, length: Int) {
        rawData.usePinned {
            require(pcap_sendpacket(handle, it.addressOf(offset).reinterpret(), length) >= 0) {
                "Error occured in pcap_sendpacket(): ${getError()}"
            }
        }
    }

    actual fun getError(): String? = pcap_geterr(handle)?.toKString()

    actual fun close() {
        pcap_close(handle)
    }
}
