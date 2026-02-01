package com.example.javaLearning.LLD.problems.parkingLot.spotManager;

import com.example.javaLearning.LLD.problems.parkingLot.entity.Spot;
import com.example.javaLearning.LLD.problems.parkingLot.strategies.spotSelecterStrategy.SpotSelectorStrategy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TwoWheelerSpotManager extends SpotManager {

  private List<Spot> spotList;
  private SpotSelectorStrategy spotSelectorStrategy;
  private ReentrantLock reentrantLock;

  @Override
  public Spot park() {

    reentrantLock.lock();
    try {
      Spot spot = spotSelectorStrategy.chooseSpot(spotList);
      log.info("Spot is booked, spot id is: {}", spot.getId());
      spot.occupySlot();
      return spot;
    } catch (Exception e) {
      log.error("error: ", e);
    } finally {
      reentrantLock.unlock();
    }

    log.info("No slot is empty.....");

    return null;
  }

  @Override
  public void unPark(Spot spot) {

    reentrantLock.lock();
    try {
      log.info("Spot: spot is freed, slot is: {}", spot.getId());
      spot.releaseSlot();
    } catch (Exception e) {
      log.error("Error", e);
    } finally {
      reentrantLock.unlock();
    }
  }

  @Override
  public Boolean hasFreeSpace() {

    for (Spot spot : spotList) {
      if (Boolean.TRUE.equals(spot.isSlotAvailable())) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }
}
