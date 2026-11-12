/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.suppliedTypes.gradleWrapper

import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin


class KoneCompilerPluginGradleWrapper : DevKitSupportPlugin(KonePluginInfo.PLUGIN_INFO)