package com.github.sunrise66.tinyidserver.dao.repository;

import com.github.sunrise66.tinyidserver.dao.entity.TinyIdToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TinyIdTokenRepository : JpaRepository<TinyIdToken, Long>, JpaSpecificationExecutor<TinyIdToken> {

}