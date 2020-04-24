package cn.hayring.sharingmachine.dao;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.junit.Test;

public class SynchronizedBiMapTest {
    @Test
    public void bimapTest() {
        BiMap biMap = HashBiMap.create();
        BiMap synBiMap = Maps.synchronizedBiMap(biMap);
        System.out.println(biMap.getClass());
        System.out.println(synBiMap.getClass());

    }
}
