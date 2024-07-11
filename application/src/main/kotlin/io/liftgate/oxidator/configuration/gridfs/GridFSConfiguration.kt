package io.liftgate.oxidator.configuration.gridfs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.gridfs.GridFsTemplate

/**
 * @author GrowlyX
 * @since 6/28/2024
 */
@Configuration
class GridFSConfiguration
{
    @Autowired lateinit var mappingConverter: MappingMongoConverter
    @Autowired lateinit var mongoDatabaseFactory: MongoDatabaseFactory

    @Bean
    fun gridFsTemplate(): GridFsTemplate = GridFsTemplate(mongoDatabaseFactory, mappingConverter)
}
