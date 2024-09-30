package com.example.limitless

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

// The @GlideModule annotation tells Glide that this class is a module.
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // You don't need to override or implement any methods if you don't need special configuration.
}
