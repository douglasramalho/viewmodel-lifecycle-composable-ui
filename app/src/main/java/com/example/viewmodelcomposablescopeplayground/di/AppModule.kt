package com.example.viewmodelcomposablescopeplayground.di

import com.example.viewmodelcomposablescopeplayground.data.SportsRepository
import com.example.viewmodelcomposablescopeplayground.ui.FeedParentViewModel
import com.example.viewmodelcomposablescopeplayground.ui.LiveMatchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val diModule = module {
    single { SportsRepository() }
    viewModel { FeedParentViewModel(get()) }
    viewModel { (matchId: String) ->
        LiveMatchViewModel(matchId, get())
    }
}
