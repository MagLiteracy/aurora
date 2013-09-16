package com.paypal.asgard

import com.paypal.asgard.listeners.PservListener
import com.paypal.asgard.listeners.ServiceListener
import com.paypal.asgard.listeners.ServiceListenerFactory

import java.beans.Introspector

class EventSubscribeService {

    def grailsApplication
    def lbaasService

    void initialize() {
        addListener(lbaasService, 'createPool', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Create pool', 'Pool creation started')
            }
        })
        addListener(lbaasService, 'deletePool', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Delete pool', 'Pool deleting started')
            }
        })
        addListener(lbaasService, 'createVip', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Create vip', 'Vip creation started')
            }
        })
        addListener(lbaasService, 'deleteVip', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Delete vip', 'Vip deleting started')
            }
        })
        addListener(lbaasService, 'createPolicy', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Create policy', 'Policy creation started')
            }
        })
        addListener(lbaasService, 'updatePolicy', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Update policy', 'Policy updating started')
            }
        })
        addListener(lbaasService, 'deletePolicy', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Delete policy', 'Policy deleting started')
            }
        })
        addListener(lbaasService, 'addServices', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Add service', 'Service adding started')
            }
        })
        addListener(lbaasService, 'deleteService', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Delete service', 'Service deleting started')
            }
        })
        addListener(lbaasService, 'changeEnabled', new ServiceListenerFactory() {
            @Override
            ServiceListener getListener() {
                return createNewPservListener('Change enabled', 'Enabled changing started')
            }
        })
    }

    private ServiceListener createNewPservListener(String action, String description) {
        createNewServiceListener(PservListener, action, description)
    }

    private ServiceListener createNewServiceListener(Class clazz, Object... args) {
        grailsApplication.mainContext.getBean(Introspector.decapitalize(clazz.simpleName), args)
    }

    boolean addListener(Object object, String methodName, ServiceListenerFactory listenerFactory) {
        if (!object.hasProperty('listeners')) {
            object.metaClass.listeners = new HashMap<String, Set<ServiceListenerFactory>>()
        }
        if (!object.listeners.get(methodName)) {
            object.listeners.put(methodName, new HashSet<ServiceListenerFactory>())
        }
        object.listeners.get(methodName).add(listenerFactory);
    }

    boolean removeListener(Object object, String methodName, ServiceListenerFactory listenerFactory) {
        if (object.hasProperty('listeners')) {
            return object.listeners.get(methodName)?.remove(listenerFactory)
        }
        return false;
    }

}
