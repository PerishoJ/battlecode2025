package f_commanding;

import battlecode.common.MapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Just supposed to represent the area around a unit (could be sight or range)
 * in terms of delta x and y.
 *
 * This condenses loops that look like for(x) { for (y) { is x,y in range? } }
 * It saves the range check
 */
class RangePatterns {

  private static final int _MAX_RNG = 5;
  private static final int _MAX_RANGE_SQUARED = _MAX_RNG * _MAX_RNG;

  private static final List<MapLocation>[] _patterns = new ArrayList[_MAX_RANGE_SQUARED];

  static List<MapLocation> getPattern (int rangeSquared){
    if (rangeSquared < _MAX_RANGE_SQUARED){
      if(_patterns[rangeSquared]==null){ //lazy initialization
        _patterns[rangeSquared] = init(rangeSquared);
      }
      return _patterns[rangeSquared];
    } else {
      return null;
    }
  }

  private static List<MapLocation> init(int rangeSquared){
    List<MapLocation> pattern = new ArrayList<>(25);//a little big, but I really don't want a resize hit
    for(int x = -_MAX_RNG; x< _MAX_RNG; x++){
      for(int y = -_MAX_RNG; y< _MAX_RNG; y++){
        if(x*x + y*y <= rangeSquared){ // Good ol' Pythagorean theorem
            pattern.add(new MapLocation(x,y));
        }
      }
    }
    return pattern;
  }

}
