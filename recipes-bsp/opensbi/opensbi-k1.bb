SUMMARY = "RISC-V Open Source Supervisor Binary Interface (OpenSBI)"
DESCRIPTION = "OpenSBI aims to provide an open-source and extensible implementation of the RISC-V SBI specification for a platform specific firmware (M-mode) and a general purpose OS, hypervisor or bootloader (S-mode or HS-mode). OpenSBI implementation can be easily extended by RISC-V platform or System-on-Chip vendors to fit a particular hadware configuration."
HOMEPAGE = "https://github.com/riscv/opensbi"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=42dd9555eb177f35150cf9aa240b61e5"

DEPENDS = "u-boot-mkimage-native dtc-native"

PROVIDES += "opensbi"

require recipes-bsp/opensbi/opensbi-payloads.inc

inherit autotools-brokensep deploy

RISCV_SBI_PLAT ?= "generic"

SRCREV = "afc26173a776a9b4b4a7730dc17f9c0841f95cba"
SRC_URI = "git://github.com/cyyself/opensbi.git;branch=k1-opensbi;protocol=https"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

RISCV_SBI_FW_TEXT_START ??= "0x80000000"
EXTRA_OEMAKE += "PLATFORM=${RISCV_SBI_PLAT} I=${D} FW_TEXT_START=${RISCV_SBI_FW_TEXT_START}"

# If RISCV_SBI_PAYLOAD is set then include it as a payload
EXTRA_OEMAKE:append = " ${@riscv_get_extra_oemake_image(d)}"
EXTRA_OEMAKE:append = " ${@riscv_get_extra_oemake_fdt(d)}"

# Required if specifying a custom payload
do_compile[depends] += "${@riscv_get_do_compile_depends(d)}"

do_install:append() {
	# In the future these might be required as a dependency for other packages.
	# At the moment just delete them to avoid warnings
	rm -r ${D}/include
	rm -r ${D}/lib*
	rm -r ${D}/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/payloads
}

do_deploy () {
	install -m 755 ${D}/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_payload.* ${DEPLOY_DIR}/
	install -m 755 ${D}/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_jump.* ${DEPLOY_DIR}/
	install -m 755 ${D}/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_dynamic.* ${DEPLOY_DIR}/
        install -m 755 ${B}/build/platform/${RISCV_SBI_PLAT}/firmware/fw_dynamic.itb ${DEPLOY_DIR}/
}

addtask deploy before do_build after do_install

FILES:${PN} += "/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_jump.*"
FILES:${PN} += "/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_payload.*"
FILES:${PN} += "/share/opensbi/*/${RISCV_SBI_PLAT}/firmware/fw_dynamic.*"

COMPATIBLE_HOST = "(riscv64|riscv32).*"
INHIBIT_PACKAGE_STRIP = "1"
