package pcap

enum class PcapAddressFamily(
    val length: Int
) {
    INET4(4),
    INET6(16),
    MAC(6)
}
