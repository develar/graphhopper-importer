package org.develar.gh

import com.graphhopper.reader.dem.MultiSourceElevationProvider
import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.util.*
import com.graphhopper.routing.util.parsers.*
import com.graphhopper.util.PMap
import com.graphhopper.util.Parameters

class Generator {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val graphHopper = GraphHopperOSM(null).forServer()
      graphHopper.graphHopperLocation = System.getProperty("graph.location")
      graphHopper.setSortGraph(true)

      val encodingManagerBuilder = EncodingManager.Builder()
      encodingManagerBuilder.add(OSMRoadClassParser())
      encodingManagerBuilder.add(OSMRoadClassLinkParser())
      encodingManagerBuilder.add(OSMRoadEnvironmentParser())
      encodingManagerBuilder.add(OSMMaxSpeedParser())
      encodingManagerBuilder.add(OSMRoadAccessParser())

      encodingManagerBuilder.add(OSMSurfaceParser())
      encodingManagerBuilder.add(OSMGetOffBikeParser())

      encodingManagerBuilder.add(Bike2WeightFlagEncoder(PMap("")))
      encodingManagerBuilder.add(MountainBikeFlagEncoder(PMap("")))
      encodingManagerBuilder.add(RacingBikeFlagEncoder(PMap("")))
      encodingManagerBuilder.add(HikeFlagEncoder(PMap("")))
      encodingManagerBuilder.add(CarFlagEncoder(PMap("")))

      graphHopper.dataReaderFile = System.getProperty("datareader.file")
      graphHopper.encodingManager = encodingManagerBuilder.build()

      val elevationProvider = MultiSourceElevationProvider(System.getProperty("graph.elevation.cache_dir"))
      graphHopper.elevationProvider = elevationProvider

      graphHopper.chFactoryDecorator.preparationThreads = Integer.getInteger(Parameters.CH.PREPARE + "threads", 1)
      graphHopper.chFactoryDecorator.addCHProfileAsString("fastest")

      graphHopper.importAndClose()
    }
  }
}