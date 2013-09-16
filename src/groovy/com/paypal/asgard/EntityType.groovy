package com.paypal.asgard

import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.ImmutableSet
import com.paypal.asgard.model.*
import groovy.transform.Immutable

import java.lang.reflect.Field
import java.lang.reflect.Modifier

@Immutable class EntityType<T> {

    // By convention, entity names match corresponding controller names.
    static final EntityType<Image> image = create('Image', { it.id })
    static final EntityType<Volume> volume = create('Volume', { it.id })
    static final EntityType<VolumeType> volumeType = create('VolumeType', {it.id})
    static final EntityType<Instance> instance = create('Instance', { it.instanceId })
    static final EntityType<Stack> heat = create('Stack', { it.id })
    static final EntityType<Network> network = create('Network', { it.id },'','Show details')
    static final EntityType<Network> router = create('Router', { it.id })
    static final EntityType<Snapshot> snapshot = create('Snapshot', { it.id })
    static final EntityType<SecurityGroup> securityGroup = create('Security Group', { it.id })
    static final EntityType<Tenant> tenant = create('Tenant', { it.id })
    static final EntityType<String> domain = create('SimpleDB Domain', { it }, '',
            'Show metadata about this SimpleDB domain')
    static final EntityType<FastProperty> fastProperty = create('Fast Property', { it.id }, '', '',
            { Map attrs, String objectId -> attrs.params = [name: objectId] })
    static final EntityType<String> terminationPolicyType = create('Termination Policy Type', { it })
    static final EntityType<OpenStackUser> openStackUser = create('Openstack User', { it.id })
    static final EntityType<String> lbaas = create('Policy', { it.id },'','Edit this policy')

    /**
     * Create an EntityType with specific attributes
     *
     * @param displayName wording used to describe this type of object to the user
     * @param keyer used to generate a String that will be used to cache this type of object
     * @param idPrefix used to identify AWS object types in the ID
     * @param linkPurpose reason the link exists
     * @param entitySpecificLinkGeneration link generation attribute modification specific to this type of object
     * @return constructed EntityType
     */
    static <T> EntityType<T> create(String displayName, Closure<String> keyer, String idPrefix = '', String linkPurpose = '',
                                    Closure entitySpecificLinkGeneration = { Map attrs, String objectId -> }) {
        new EntityType<T>(displayName, keyer, idPrefix, linkPurpose ?: "Show details of this ${displayName}",
                entitySpecificLinkGeneration)
    }

    private static final Collection<EntityType> allEntityTypes
    private static final ImmutableBiMap<String, EntityType> nameToEntityType
    static {
        Collection<Field> entityTypeFields = EntityType.declaredFields.findAll {
            Modifier.isStatic(it.modifiers) && it.type == EntityType
        }
        ImmutableBiMap.Builder<String, EntityType> nameToEntityTypeBuilder =
            new ImmutableBiMap.Builder<String, EntityType>()
        Collection<EntityType> entityTypes = []
        for (Field field : entityTypeFields) {
            EntityType type = EntityType[field.name] as EntityType
            entityTypes << type
            nameToEntityTypeBuilder.put(field.name, type)
        }
        allEntityTypes = ImmutableSet.copyOf(entityTypes)
        nameToEntityType = nameToEntityTypeBuilder.build()
    }

    static Collection<EntityType> values() {
        allEntityTypes
    }

    static EntityType fromName(String name) {
        nameToEntityType.get(name)
    }

    static String nameOf(EntityType entityType) {
        nameToEntityType.inverse().get(entityType)
    }

    static EntityType fromId(String id) {
        values().find { it.idPrefix && id?.startsWith(it.idPrefix) }
    }

    String displayName
    Closure<String> keyer
    String idPrefix
    String linkPurpose
    Closure entitySpecificLinkGeneration

    /**
     * The unique String key for the value object.
     *
     * @param an entity of type T
     * @return The unique String key
     */
    String key(T entity) {
        keyer(entity)
    }

    String name() {
        EntityType.nameOf(this)
    }

    /**
     * If id is provided and it lacks the correct prefix, add the prefix. If the prefix is already present or id is
     * blank then make no changes to the id.
     *
     * @param input the provided id string
     * @return String the correct id
     */
    String ensurePrefix(String input) {
        if (!idPrefix) {
            return input
        }
        String id = input?.trim()
        id?.startsWith(idPrefix) || !id ? id : "${idPrefix}${id}"
    }
}