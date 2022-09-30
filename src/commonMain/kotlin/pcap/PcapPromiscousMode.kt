package pcap

enum class PcapPromiscousMode(
    val value: Int
) {
    PROMISCUOUS(1),
    NONPROMISCUOUS(0)
}
