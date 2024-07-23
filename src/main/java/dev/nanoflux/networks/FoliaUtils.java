package dev.nanoflux.networks;

import dev.nanoflux.networks.utils.BlockLocation;
import org.bukkit.Location;
import org.bukkit.Server;

import java.lang.reflect.Method;


public class FoliaUtils {

    public static boolean folia;

    static Class RegionizedServer;

    static {
        try {
            RegionizedServer = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            //Region = Class.forName("io.papermc.paper.threadedregions.Region");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
    }

//    public static boolean areBlocksInSameRegion(Location loc1, Location loc2) throws NoSuchFieldException, IllegalAccessException {
//        Object regionizedServer = RegionizedServer.getField("getRegionizedServer").get(null);
////        Object region1 = regionizedServer.getRegionForLocation(loc1);
////        Object region2 = regionizedServer.getRegionForLocation(loc2);
//        System.out.println(regionizedServer);
//        System.out.println(regionizedServer.getClass());
//        return false;
//        //return region1 == region2;
//    }

//    public static boolean areLocationsInSameRegion(Location loc1, Location loc2) {
//
//        if (!folia) {
//           return true;
//        }
//
//        try {
//
//            // Get the getRegionizedServer method
//            java.lang.reflect.Method getRegionizedServerMethod = RegionizedServer.getMethod("getRegionizedServer");
//
//            // Invoke the static method to get the instance
//            Object regionizedServer = getRegionizedServerMethod.invoke(null);
//
//            // Get the getRegionForLocation method
//            java.lang.reflect.Method getRegionForLocationMethod = RegionizedServer.getMethod("getRegionForLocation", Location.class);
//
//            // Get regions for both locations
//            Object region1 = getRegionForLocationMethod.invoke(regionizedServer, loc1);
//            Object region2 = getRegionForLocationMethod.invoke(regionizedServer, loc2);
//
//            // Compare the regions
//            return region1 == region2;
//        } catch (Exception e) {
//            // Handle other exceptions (NoSuchMethodException, IllegalAccessException, InvocationTargetException, etc.)
//            e.printStackTrace();
//            return false; // or handle it differently based on your requirements
//        }
//    }

//    public static boolean areLocationsInSameRegion2(Location loc1, Location loc2) {
//        try {
//            // Check if Folia is running by attempting to load the RegionizedServer class
//            Class<?> regionizedServerClass = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
//
//            // Get the getRegionizedServer method
//            Method getRegionizedServerMethod = regionizedServerClass.getMethod("getRegionizedServer");
//
//            // Invoke the static method to get the instance
//            Object regionizedServer = getRegionizedServerMethod.invoke(null);
//
//            // Get the getRegionForLocation method
//            Method getRegionForLocationMethod = regionizedServerClass.getMethod("getRegionForLocation", Location.class);
//
//            // Get regions for both locations
//            Object region1 = getRegionForLocationMethod.invoke(regionizedServer, loc1);
//            Object region2 = getRegionForLocationMethod.invoke(regionizedServer, loc2);
//
//            // Compare the regions
//            return region1.equals(region2);
//        } catch (ClassNotFoundException e) {
//            // RegionizedServer class doesn't exist, Folia is not running
//            return false;
//        } catch (Exception e) {
//            // Handle other exceptions (NoSuchMethodException, IllegalAccessException, InvocationTargetException, etc.)
//            e.printStackTrace();
//            return false;
//        }
//    }


//    public static boolean areBlocksInSameRegion(BlockLocation loc1, BlockLocation loc2) {
//        RegionizedServer regionizedServer = RegionizedServer.getRegionizedServer();
//        Region region1 = regionizedServer.getRegionForLocation(loc1.getBukkitLocation());
//        Region region2 = regionizedServer.getRegionForLocation(loc2.getBukkitLocation());
//
//        return region1 == region2;
//    }


}
