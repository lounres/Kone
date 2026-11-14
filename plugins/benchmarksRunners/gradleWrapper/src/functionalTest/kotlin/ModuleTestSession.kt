/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import de.infix.testBalloon.framework.core.TestCompartment
import de.infix.testBalloon.framework.core.TestSession


class ModuleTestSession : TestSession(
    defaultCompartment = { TestCompartment.Concurrent }
)