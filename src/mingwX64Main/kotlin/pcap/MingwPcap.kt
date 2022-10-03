package pcap

import kotlinx.cinterop.*

actual object Pcap {
    actual val libVersion: String
        get() = requireNotNull(pcap_lib_version()).toKString()

    actual fun lookupDev(): PcapNetworkInterface? = memScoped {
        val buf = allocArray<ByteVar>(1024)
        val name = pcap_lookupdev(buf.pointed.ptr)?.toKString()
        findAllDevs().find { it.name == name }
    }

    actual fun findAllDevs(): List<PcapNetworkInterface> = memScoped {
        val alldevs = alloc<CPointerVar<pcap_if_t>>()
        val errbuf = errbuf()
        val rc = pcap_findalldevs(alldevs.ptr, errbuf)
        require(rc == 0) { "$rc ${errbuf.toKString()}" }
        val networkInterfaces = ArrayList<PcapNetworkInterface>()
        var dev: pcap_if? = alldevs.pointed
        while (dev != null) {
            val addresses = pcapAddresses(dev.addresses)
            val mac = getMac(dev.name?.toKString().toString())
            val networkInterface = PcapNetworkInterface(
                dev.name?.toKString() ?: "",
                dev.description?.toKString() ?: "",
                if (mac != null) addresses + mac else addresses,
            )
            networkInterfaces.add(networkInterface)
            dev = dev.next?.pointed
        }
        networkInterfaces
    }

    // TODO: make by code
    private fun getMac(name: String): PcapAddress? {
        try {
            if (!name.startsWith("\\Device\\NPF_{")) return null
            val guid = name.substring(12, 50)
            val cmd = "wmic path Win32_NetworkAdapter where \"GUID like '$guid'\" get MacAddress"
            val result = cmd(cmd)
            if (!result.startsWith("MACAddress")) return null
            val resultLines = result.split(" ", "\n", "\r").filter { it.isNotBlank() }
            println("result: $resultLines")
            val mac = resultLines.getOrNull(1)?.split(":")?.map {
                it.toInt(16).toByte()
            }?.toByteArray() ?: return null
            return PcapAddress(PcapAddressFamily.MAC, mac)
        } catch(e: Exception) {
            throw IllegalArgumentException("Can't get mac for interface: $name", e)
        }
    }
}
