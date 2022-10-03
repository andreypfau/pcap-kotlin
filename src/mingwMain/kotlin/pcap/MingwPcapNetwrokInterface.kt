package pcap

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString

actual class PcapNetworkInterface actual constructor(
    actual val name: String,
    actual val description: String,
    actual val addresses: List<PcapAddress>
) {
    constructor(pcapIf: pcap_if) : this(
        pcapIf.name!!.toKString(),
        pcapIf.description!!.toKString(),
        pcapAddresses(pcapIf.addresses)
    )

    actual fun openLive(
        snapLen: Int,
        mode: PcapPromiscousMode,
        timeoutMillis: Int
    ): PcapHandle = memScoped {
        val errBuf = errbuf()
        val handle = requireNotNull(pcap_open_live(name, snapLen, mode.value, timeoutMillis, errBuf)) {
            errBuf.toKString()
        }
        PcapHandle(handle)
    }

    override fun toString(): String =
        "PcapNetworkInterface(name='$name', description='$description', addresses=$addresses)"
}
