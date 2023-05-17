/*
 * MIT License
 *
 * Copyright (c) 2023 Arematics UG (haftungsbeschränkt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without restriction,including without limitation
 *   the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *    permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *   ARISING FROM, OUT OF OR IN CONNECTION WITH# THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.arematics.enhancements.domain;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SimpleInventoryInfo {
    private final String title;
    private GenericContainerScreenHandler screenHandler;
    private final SimpleInventory simpleInventory;

    public SimpleInventoryInfo(String title, SimpleInventory simpleInventory) {
        this.title = title;
        this.simpleInventory = simpleInventory;
    }

    public String title() {
        return title;
    }

    public SimpleInventory inventory() {
        return simpleInventory;
    }

    public GenericContainerScreenHandler screenHandler() {
        return screenHandler;
    }

    public void setScreenHandler(GenericContainerScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
    }
}
