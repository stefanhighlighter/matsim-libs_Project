/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package pl.poznan.put.vrp.dynamic.data.schedule.impl;

import java.util.*;

import pl.poznan.put.vrp.dynamic.data.model.Vehicle;
import pl.poznan.put.vrp.dynamic.data.network.Vertex;
import pl.poznan.put.vrp.dynamic.data.schedule.*;
import pl.poznan.put.vrp.dynamic.data.schedule.Task.TaskStatus;


public class ScheduleImpl<T extends AbstractTask>
    implements Schedule<T>
{
    private final Vehicle vehicle;

    private final List<T> tasks;
    private final List<T> unmodifiableTasks;

    private ScheduleStatus status;
    private T currentTask;


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ScheduleImpl(Vehicle vehicle)
    {
        this.vehicle = vehicle;

        //TODO what about LinkedList??
        tasks = new ArrayList<T>();
        unmodifiableTasks = (List)Collections.unmodifiableList(tasks);

        status = ScheduleStatus.UNPLANNED;
        currentTask = null;
    }


    @Override
    public Vehicle getVehicle()
    {
        return vehicle;
    }


    @Override
    public List<T> getTasks()
    {
        return unmodifiableTasks;
    }


    @Override
    public int getTaskCount()
    {
        return tasks.size();
    }


    public void addTask(T task)
    {
        addTask(tasks.size(), task);
    }


    public void addTask(int taskIdx, T task)
    {
        validateArgsBeforeAddingTask(taskIdx, task);

        if (status == ScheduleStatus.UNPLANNED) {
            status = ScheduleStatus.PLANNED;
        }

        tasks.add(taskIdx, task);
        task.schedule = this;
        task.taskIdx = taskIdx;
        task.status = TaskStatus.PLANNED;

        // update idx of the existing tasks
        for (int i = taskIdx + 1; i < tasks.size(); i++) {
            tasks.get(i).taskIdx = i;
        }
    }


    private void validateArgsBeforeAddingTask(int taskIdx, Task task)
    {
        int beginTime = task.getBeginTime();
        int endTime = task.getEndTime();
        Vertex beginVertex = Tasks.getBeginVertex(task);
        Vertex endVertex = Tasks.getEndVertex(task);

        failIfCompleted();

        if (status == ScheduleStatus.STARTED && taskIdx <= currentTask.getTaskIdx()) {
            throw new IllegalStateException();
        }

        int taskCount = tasks.size();

        if (taskIdx < 0 || taskIdx > taskCount) {
            throw new IllegalArgumentException();
        }

        if (beginTime > endTime) {
            throw new IllegalArgumentException();
        }

        if (taskIdx > 0) {
            Task previousTask = tasks.get(taskIdx - 1);

            if (previousTask.getEndTime() != beginTime) {
                throw new IllegalArgumentException();
            }

            if (Tasks.getEndVertex(previousTask) != beginVertex) {
                throw new IllegalArgumentException();
            }
        }
        else { // taskIdx == 0
            if (vehicle.getDepot().getVertex() != beginVertex) {
                throw new IllegalArgumentException();
            }
        }

        if (taskIdx < taskCount) {
            Task nextTask = tasks.get(taskIdx);// currently at taskIdx, but soon at taskIdx+1

            if (nextTask.getBeginTime() != endTime) {
                throw new IllegalArgumentException();
            }

            if (Tasks.getBeginVertex(nextTask) != endVertex) {
                throw new IllegalArgumentException();
            }
        }
    }


    @Override
    public void removeLastTask()
    {
        removeTaskImpl(tasks.size() - 1);
    }


    @Override
    public void removeTask(T task)
    {
        removeTaskImpl(task.getTaskIdx());
    }


    private void removeTaskImpl(int taskIdx)
    {
        failIfUnplanned();
        failIfCompleted();

        AbstractTask task = tasks.get(taskIdx);

        if (task.getStatus() != TaskStatus.PLANNED) {
            throw new IllegalStateException();
        }

        tasks.remove(taskIdx);

        for (int i = taskIdx; i < tasks.size(); i++) {
            tasks.get(i).taskIdx = i;
        }

        if (tasks.size() == 0) {
            status = ScheduleStatus.UNPLANNED;
        }
    }


    @Override
    public T getCurrentTask()
    {
        failIfNotStarted();
        return currentTask;
    }


    @Override
    public T nextTask()
    {
        failIfUnplanned();
        failIfCompleted();

        int nextIdx;

        if (status == ScheduleStatus.PLANNED) {
            status = ScheduleStatus.STARTED;
            nextIdx = 0;
        }
        else { // STARTED
            currentTask.status = TaskStatus.PERFORMED;
            nextIdx = currentTask.taskIdx + 1;
        }

        if (nextIdx == tasks.size()) {
            currentTask = null;
            status = ScheduleStatus.COMPLETED;
        }
        else {
            currentTask = tasks.get(nextIdx);
            currentTask.status = TaskStatus.STARTED;
        }

        return currentTask;
    }


    @Override
    public ScheduleStatus getStatus()
    {
        return status;
    }


    @Override
    public int getBeginTime()
    {
        failIfUnplanned();
        return tasks.get(0).getBeginTime();
    }


    @Override
    public int getEndTime()
    {
        failIfUnplanned();
        return tasks.get(tasks.size() - 1).getEndTime();
    }


    @Override
    public String toString()
    {
        return "Schedule_" + vehicle.getId();
    }


    private void failIfUnplanned()
    {
        if (status == ScheduleStatus.UNPLANNED) {
            throw new IllegalStateException();
        }
    }


    private void failIfCompleted()
    {
        if (status == ScheduleStatus.COMPLETED) {
            throw new IllegalStateException();
        }
    }


    private void failIfNotStarted()
    {
        if (status != ScheduleStatus.STARTED) {
            throw new IllegalStateException();
        }
    }
}
