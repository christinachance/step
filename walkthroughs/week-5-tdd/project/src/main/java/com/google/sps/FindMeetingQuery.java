// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.*; 


/*
for the coding challenge, keep the times for the mandatory, and then create a seperate scedule with possbible times, make it a 
dictionary so we can keep track of how many people can attend from the optional that time and then school teh times withe the largest number of people
*/

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request){
        ArrayList<TimeRange> busyMandatory = new ArrayList<>();
        ArrayList<TimeRange> busyOptional = new ArrayList<>();
        ArrayList<TimeRange> availableMandatory = new ArrayList<>();
        ArrayList<TimeRange> availableOptional = new ArrayList<>();
        ArrayList<TimeRange> available = new ArrayList<>();

        //Mandatory Attendees
        // adds all time ranges when attendees are busy
        for(Event event : events){
            for(String attendee : request.getAttendees()){
                //adding time frame only if not in there and one attendee is also an attendee of event
                if(event.getAttendees().contains(attendee) && !busyMandatory.contains(event.getWhen())){
                    busyMandatory.add(event.getWhen());
                    break;
                }
            }
        }

        Collections.sort(busyMandatory, TimeRange.ORDER_BY_START);

        //iterate over the list of busy and if an event is containd in another, remove it.
        Iterator<TimeRange> iter = busyMandatory.iterator();
        while(iter.hasNext()){
            TimeRange current = iter.next();
            for(int checkIndex=0; checkIndex <busyMandatory.size(); checkIndex++){
                if(current.start() > busyMandatory.get(checkIndex).start() && current.end() < busyMandatory.get(checkIndex).end()){
                    iter.remove();
                }
            }
        }

        //Optional Attendees
        for(Event event : events){
            for(String attendee : request.getOptionalAttendees()){
                if(event.getAttendees().contains(attendee) && !busyMandatory.contains(event.getWhen())){
                    busyOptional.add(event.getWhen());
                    break;
                }
            }
        }

        Collections.sort(busyOptional, TimeRange.ORDER_BY_START);

        Iterator<TimeRange> iterOptional = busyOptional.iterator();
        while(iter.hasNext()){
            TimeRange current = iterOptional.next();
            for(int checkIndex=0; checkIndex <busyOptional.size(); checkIndex++){
                if(current.start() > busyOptional.get(checkIndex).start() && current.end() < busyOptional.get(checkIndex).end()){
                    iterOptional.remove();
                }
            }
        }

        //checks availabilty of optional attendees
        if(request.getAttendees().isEmpty() && !request.getOptionalAttendees().isEmpty()){
            return getAvailability(busyOptional, request);

        } else if (!request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()){
            return getAvailability(busyMandatory, request);

        } else {
            availableMandatory = getAvailability(busyMandatory, request);
            availableOptional = getAvailability(busyOptional, request);

            if(availableMandatory.isEmpty() && !availableOptional.isEmpty()){
                return availableOptional;
            } else if (availableOptional.isEmpty() && !availableMandatory.isEmpty()){
                return availableMandatory;
            } else {
                for(TimeRange optional : availableOptional){
                    for(TimeRange mandatory : availableMandatory){
                        if(mandatory.contains(optional)){
                            if(optional.duration() == Math.min(optional.duration(), mandatory.duration()) && !available.contains(TimeRange.fromStartDuration(optional.start(), optional.duration()))){
                                available.add(TimeRange.fromStartDuration(optional.start(), optional.duration()));
                            } else {
                                if(!available.contains(TimeRange.fromStartDuration(mandatory.start(), mandatory.duration()))){
                                    available.add(TimeRange.fromStartDuration(mandatory.start(), mandatory.duration()));
                                }
                            }
                        }
                        if(mandatory.overlaps(optional) && request.getDuration() <= (Math.min(optional.end(), mandatory.end())- Math.max(optional.start(), mandatory.start())-1)){
                            if(!available.contains(TimeRange.fromStartEnd(Math.max(optional.start(), mandatory.start()), Math.min(optional.end(), mandatory.end()), false))){
                                available.add(TimeRange.fromStartEnd(Math.max(optional.start(), mandatory.start()), Math.min(optional.end(), mandatory.end()), false));
                            }
                        }
                    }
                }
                if(available.isEmpty()){
                    return availableMandatory;
                } else {
                    return available;
                }
            }
        }
    }

    private ArrayList<TimeRange> getAvailability(ArrayList<TimeRange> busyTimes, MeetingRequest request){
        ArrayList<TimeRange> availableTimes = new ArrayList<>();

        if(request.getDuration() > TimeRange.END_OF_DAY){
            return availableTimes;
        }
        
        if(busyTimes.isEmpty()){
            availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
            return availableTimes;
        }

        if(busyTimes.size()==1){
            if(busyTimes.get(0) == TimeRange.WHOLE_DAY){
                return availableTimes;
            } else {
                availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(), false));
                availableTimes.add(TimeRange.fromStartEnd(busyTimes.get(busyTimes.size()-1).end(), TimeRange.END_OF_DAY, true));
                return availableTimes;
            }
        }

        if(busyTimes.get(0).start()!= 0){
            availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(), false));
        }

        //adds possible time range for the avaiable times when none of the groups meetings overlap and ensures that the duration in available  times is more or equivalent to requested duration
        for( int timeIndex = 1; timeIndex < busyTimes.size(); timeIndex++){
            if(!busyTimes.get(timeIndex).overlaps(busyTimes.get(timeIndex-1)) && request.getDuration()<=(busyTimes.get(timeIndex).start()) - busyTimes.get(timeIndex-1).end()){
                availableTimes.add(TimeRange.fromStartEnd(busyTimes.get(timeIndex-1).end(), busyTimes.get(timeIndex).start(), false));
            }
        }

        //adds all the time after the last meeting of the group of attendees
        if((TimeRange.END_OF_DAY - busyTimes.get(busyTimes.size()-1).end()-1) > 0 ){
            availableTimes.add(TimeRange.fromStartEnd(busyTimes.get(busyTimes.size()-1).end(), TimeRange.END_OF_DAY, true));
        }

        return availableTimes;
    }

}
