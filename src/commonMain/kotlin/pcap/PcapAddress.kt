package pcap

class PcapAddress(
    val family: PcapAddressFamily,
    data: ByteArray
) {
    private val _data = data.copyOf()

    fun toByteArray(): ByteArray = _data.copyOf()
    fun toByteArray(destination: ByteArray, offset: Int = 0): ByteArray {
        return _data.copyInto(destination, offset)
    }

    override fun toString(): String = "PcapAddress(family=$family, data=${_data.hex()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PcapAddress) return false
        if (family != other.family) return false
        println("equals: $this <-> $other = ${_data.contentEquals(other._data)}")
        if (!_data.contentEquals(other._data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = family.hashCode()
        result = 31 * result + _data.contentHashCode()
        return result
    }
}
