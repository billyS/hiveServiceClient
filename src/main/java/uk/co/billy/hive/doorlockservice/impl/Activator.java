package uk.co.billy.hive.doorlockservice.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import uk.co.billy.hive.doorlockservice.api.DoorLockServiceAPI;

public class Activator implements BundleActivator {

	private BundleContext m_context = null;
    private ServiceTracker doorLockTracker;
    private ClientDoorlockGUI gui;
    private long bundleID=0;
    private Map<DoorLockServiceAPI, String> doorlockServices = new ConcurrentHashMap<DoorLockServiceAPI, String>();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> handle;
	 
	public void start( BundleContext context) {
		
		m_context = context;
		bundleID = m_context.getBundle().getBundleId();
		System.out.println("Consumer says : Checking Bundle ID " + bundleID);
		doorLockTracker = new ServiceTracker(m_context, DoorLockServiceAPI.class.getName(), null) {
        	
            @Override
            public Object addingService(ServiceReference reference) {
            	
                Object service = super.addingService(reference);
                
                if (service instanceof DoorLockServiceAPI) {
                	
                	 DoorLockServiceAPI dls = (DoorLockServiceAPI) service;
                	 System.out.println("Consumer says : Adding DoorLockkService: " + " (" + dls + ")");
                	 doorlockServices.put(dls, "TestDoorLockService");
                	 startGUI(dls);
                }
                return service;
            }
            
            public void removedService(ServiceReference reference, Object service) {
                String value = doorlockServices.remove(service);
                System.out.println("Removed doorlockService: " + value);
                super.removedService(reference, service);
            }
        };
        doorLockTracker.open();
        
        /*scheduler = Executors.newScheduledThreadPool(1);
        Runnable printer = new Runnable() {
            int counter;
            public void run() {
                counter++;
                String text = "some text " + counter;
                System.out.println("Sending text to displays: " + text);
                DoorLockServiceAPI service = null;
                for (Entry<DoorLockServiceAPI, String> entry : doorlockServices.entrySet()) {
                    try {
                    	//entry.getKey().startService();
                    	service = entry.getKey();
                    	service.startService();
                       
                        System.out.println("Current door state " + entry.getKey().getDoorState());
                    } catch (Throwable th) {
                        System.out.println("Could not send message to display: " + entry.getValue());
                    }
                }
                gui = new ClientDoorlockGUI(service);
            }
        };
        handle = scheduler.scheduleAtFixedRate(printer, 5, 5, TimeUnit.SECONDS);*/	
	}
	
	public void startGUI(DoorLockServiceAPI service) {
		if(doorlockServices.size() > 0){
			for (Entry<DoorLockServiceAPI, String> entry : doorlockServices.entrySet()) {
                try {
                	
                	service = entry.getKey();
                	service.startService();
                	System.out.println(service.getClass());
                	System.out.println(service.getClass().getName());
                	System.out.println(service.getClass().getComponentType());
                    System.out.println("Current door state " + entry.getKey().getDoorState());
                } catch (Throwable th) {
                    System.out.println("Could not send message to display: " + entry.getValue());
                }
            }
            gui = new ClientDoorlockGUI(service);
		}
	}
	
	public void stop(BundleContext context) throws Exception
	{
		doorLockTracker.close();
	}

}
