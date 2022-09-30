package pcap

expect class PcapHandle {
    fun nextPacket(
        rawData: ByteArray,
        offset: Int = 0,
        length: Int = rawData.size - offset
    ): Int

    fun sendPacket(
        rawData: ByteArray,
        offset: Int = 0,
        length: Int = rawData.size - offset
    )

    fun getError(): String?

    fun close()
}
