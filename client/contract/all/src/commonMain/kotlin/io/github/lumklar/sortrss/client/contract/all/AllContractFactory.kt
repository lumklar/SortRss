package io.github.lumklar.sortrss.client.contract.all

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory

interface AllContractFactory {
    fun data(): DataContractFactory;
}