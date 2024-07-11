package io.liftgate.oxidator.validation.defense

import io.liftgate.oxidator.validation.LicenseValidationMethod

object EnvironmentCheckMethod : LicenseValidationMethod
{
    override fun requiresMore() = true
    override fun tryValidate(licenseContent: String): Boolean {
        return !isRunningInVM() && !isDebuggerAttached()
    }

    private fun isRunningInVM(): Boolean {
        val vmNames = listOf("Oracle Corporation", "VMware, Inc.", "Microsoft Corporation", "Xen")
        return vmNames.any { it in System.getProperty("java.vm.vendor") }
    }

    private fun isDebuggerAttached(): Boolean {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString()
            .contains("-agentlib:jdwp")
    }
}
