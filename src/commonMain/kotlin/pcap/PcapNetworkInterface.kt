package pcap

expect class PcapNetworkInterface(
     name: String,
     description: String,
     addresses: List<PcapAddress>
) {
    val name: String
    val description: String
    val addresses: List<PcapAddress>

    fun openLive(
        snapLen: Int = UShort.MAX_VALUE.toInt(),
        mode: PcapPromiscousMode = PcapPromiscousMode.PROMISCUOUS,
        timeoutMillis: Int = 10
    ): PcapHandle
}
