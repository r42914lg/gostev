package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserManager {
    val isAuthenticated: Flow<Boolean>
    val userData: Flow<User?>
}
