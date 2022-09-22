package pcap

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString

data class PcapNetworkInterface(
    val name: String,
    val description: String,
    val addresses: List<PcapAddress>
) {
    constructor(pcapIf: pcap_if) : this(
        pcapIf.name!!.toKString(),
        pcapIf.description!!.toKString(),
        PcapAddress.newInstances(pcapIf.addresses)
    )

    fun openLive(
        snapLen: Int = UShort.MAX_VALUE.toInt(),
        mode: PromiscuousMode = PromiscuousMode.PROMISCUOUS,
        timeoutMillis: Int = 10
    ): PcapHandle = memScoped {
        val errBuf = errbuf()
        val handle = requireNotNull(pcap_open_live(name, snapLen, mode.value, timeoutMillis, errBuf)) {
            errBuf.toKString()
        }
        PcapHandle(handle)
    }

    enum class PromiscuousMode(
        val value: Int
    ) {
        PROMISCUOUS(1),
        NONPROMISCUOUS(0)
    }
}
