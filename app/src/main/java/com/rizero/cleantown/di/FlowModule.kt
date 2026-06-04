package com.rizero.cleantown.di


import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module
@Configuration
@ComponentScan("com.rizero.cleantown.component")
class FlowModule