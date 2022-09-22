package pcap

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed

class PcapAddress internal constructor(
    val family: AddressFamily,
    private val _data: ByteArray
) {
    val data: ByteArray get() = _data.copyOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PcapAddress) return false
        if (family != other.family) return false
        if (!_data.contentEquals(other._data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = family.hashCode()
        result = 31 * result + _data.contentHashCode()
        return result
    }

    override fun toString(): String =
        "PcapAddress(family=$family, data=0x${_data.hex()})"

    companion object {
        fun newInstances(
            pointer: CPointer<pcap_addr>?
        ): List<PcapAddress> {
            if (pointer == null) return emptyList()
            val addresses = ArrayList<PcapAddress>()
            var pcapAddr: pcap_addr? = pointer.pointed
            while (pcapAddr != null) {
                val addrPointer = pcapAddr.addr
                if (addrPointer != null) {
                    addresses.add(PcapAddress(addrPointer))
                }
                pcapAddr = pcapAddr.next?.pointed
            }
            return addresses
        }
    }

    enum class AddressFamily(
        val length: Int
    ) {
        INET4(4),
        INET6(16),
        MAC(6)
    }
}

expect fun PcapAddress(pointer: CPointer<sockaddr>): PcapAddress
