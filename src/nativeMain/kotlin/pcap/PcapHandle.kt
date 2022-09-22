package pcap

import kotlinx.cinterop.*
import platform.posix.memcpy
import kotlin.math.min

class PcapHandle(
    val handle: CPointer<pcap_t>,
) {
    fun nextPacket(buf: ByteArray, offset: Int = 0, length: Int = buf.size - offset): Int = memScoped {
        val header = alloc<pcap_pkthdr>()
        val packet = pcap_next(handle, header.ptr)
        if (packet != null) {
            buf.usePinned {
                it.addressOf(offset)
                val len = min(header.caplen, length.toUInt())
                memcpy(it.addressOf(offset), packet, len.convert())
                len.toInt()
            }
        } else {
            0
        }
    }

    fun sendPacket(rawData: ByteArray, offset: Int = 0, length: Int = rawData.size - offset) {
        rawData.usePinned {
            require(pcap_sendpacket(handle, it.addressOf(offset).reinterpret(), length) >= 0) {
                "Error occured in pcap_sendpacket(): ${getError()}"
            }
        }
    }

    fun getError(): String? = pcap_geterr(handle)?.toKString()

    fun close() {
        pcap_close(handle)
    }


    enum class TimestampPrecision(
        val value: Int
    ) {
        MICRO(0),
        NANO(1)
    }
}
