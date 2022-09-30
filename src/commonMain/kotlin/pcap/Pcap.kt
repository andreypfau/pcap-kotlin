package pcap

expect object Pcap {
    val libVersion: String

    fun lookupDev(): PcapNetworkInterface?

    fun findAllDevs(): List<PcapNetworkInterface>
}
