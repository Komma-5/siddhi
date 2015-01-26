/*
 * Copyright (c) 2005 - 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.wso2.siddhi.core.query.input.stream.state;

import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.state.StateEvent;
import org.wso2.siddhi.core.event.state.StateEventCloner;
import org.wso2.siddhi.core.event.state.StateEventPool;
import org.wso2.siddhi.core.event.stream.StreamEvent;
import org.wso2.siddhi.core.event.stream.StreamEventCloner;
import org.wso2.siddhi.core.event.stream.StreamEventPool;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.query.api.execution.query.input.stream.StateInputStream;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created on 12/17/14.
 */
public class StreamPreStateProcessor implements PreStateProcessor {

    protected int stateId;
    protected boolean isStartState;
    protected volatile boolean stateChanged = false;
    protected StateInputStream.Type stateType;
    protected StreamPostStateProcessor thisStatePostProcessor;

    protected Processor nextProcessor;

    protected ComplexEventChunk<StateEvent> currentStateEventChunk = new ComplexEventChunk<StateEvent>();
    protected LinkedList<StateEvent> pendingStateEventList = new LinkedList<StateEvent>();
    protected LinkedList<StateEvent> newAndEveryStateEventList = new LinkedList<StateEvent>();

    protected StateEventPool stateEventPool;
    //  private StreamEventPool streamEventPool;
    protected StreamEventCloner streamEventCloner;
    protected StateEventCloner stateEventCloner;
    protected StreamEventPool streamEventPool;

    public StreamPreStateProcessor(StateInputStream.Type stateType) {
        this.stateType = stateType;
    }

    public void setThisStatePostProcessor(StreamPostStateProcessor thisStatePostProcessor) {
        this.thisStatePostProcessor = thisStatePostProcessor;
    }

    public StreamPostStateProcessor getThisStatePostProcessor() {
        return thisStatePostProcessor;
    }

    /**
     * Process the handed StreamEvent
     *
     * @param complexEventChunk event chunk to be processed
     */
    @Override
    public void process(ComplexEventChunk complexEventChunk) {

        complexEventChunk.reset();
        StreamEvent streamEvent = (StreamEvent) complexEventChunk.next(); //Sure only one will be sent
        for (Iterator<StateEvent> iterator = pendingStateEventList.iterator(); iterator.hasNext(); ) {
            StateEvent stateEvent = iterator.next();
            stateEvent.setEvent(stateId, streamEventCloner.copyStreamEvent(streamEvent));
            process(stateEvent);
            if (stateChanged) {
                iterator.remove();
            } else {
                switch (stateType) {
                    case PATTERN:
                        stateEvent.setEvent(stateId, null);
                        break;
                    case SEQUENCE:
                        stateEvent.setEvent(stateId, null);
                        iterator.remove();
                        if (thisStatePostProcessor.callbackPreStateProcessor != null) {
                            thisStatePostProcessor.callbackPreStateProcessor.startStateReset();
                        }
                        break;
                }
            }
        }
    }

    protected void process(StateEvent stateEvent) {
        currentStateEventChunk.add(stateEvent);
        currentStateEventChunk.reset();
        stateChanged = false;
        nextProcessor.process(currentStateEventChunk);
        currentStateEventChunk.reset();
    }

    /**
     * Get next processor element in the processor chain. Processed event should be sent to next processor
     *
     * @return
     */
    @Override
    public Processor getNextProcessor() {
        return nextProcessor;
    }

    /**
     * Set next processor element in processor chain
     *
     * @param processor Processor to be set as next element of processor chain
     */
    @Override
    public void setNextProcessor(Processor processor) {
        this.nextProcessor = processor;
    }

    /**
     * Set as the last element of the processor chain
     *
     * @param processor Last processor in the chain
     */
    @Override
    public void setToLast(Processor processor) {
        if (nextProcessor == null) {
            this.nextProcessor = processor;
        } else {
            this.nextProcessor.setToLast(processor);
        }
    }

    public void init() {
        if (isStartState) {
            StateEvent stateEvent = stateEventPool.borrowEvent();
            addState(stateEvent);
        }
    }

    /**
     * Clone a copy of processor
     *
     * @return clone of StreamPreStateProcessor
     */
    @Override
    public PreStateProcessor cloneProcessor() {
        StreamPreStateProcessor streamPreStateProcessor = new StreamPreStateProcessor(stateType);
        cloneProperties(streamPreStateProcessor);
        return streamPreStateProcessor;
    }

    protected void cloneProperties(StreamPreStateProcessor streamPreStateProcessor) {
        streamPreStateProcessor.stateId = this.stateId;
        streamPreStateProcessor.stateEventPool = this.stateEventPool;
        streamPreStateProcessor.streamEventCloner = this.streamEventCloner;
        streamPreStateProcessor.stateEventCloner = this.stateEventCloner;
        streamPreStateProcessor.streamEventPool = this.streamEventPool;
    }

    @Override
    public void addState(StateEvent stateEvent) {
        //        if (stateType == StateInputStream.Type.SEQUENCE) {
        //            newAndEveryStateEventList.clear();
        //            pendingStateEventList.clear();
        //        }
        if (stateType == StateInputStream.Type.SEQUENCE) {
            if (newAndEveryStateEventList.isEmpty()) {
                newAndEveryStateEventList.add(stateEvent);
            }
        } else {
            newAndEveryStateEventList.add(stateEvent);

        }
    }

    @Override
    public void addEveryState(StateEvent stateEvent) {
        newAndEveryStateEventList.add(stateEventCloner.copyStateEvent(stateEvent));
    }

    public void stateChanged() {
        stateChanged = true;
    }

    public void setStartState(boolean isStartState) {
        this.isStartState = isStartState;
    }

    public void setStateEventPool(StateEventPool stateEventPool) {
        this.stateEventPool = stateEventPool;
    }

    public void setStreamEventPool(StreamEventPool streamEventPool) {
        this.streamEventPool = streamEventPool;
    }

    public void setStreamEventCloner(StreamEventCloner streamEventCloner) {
        this.streamEventCloner = streamEventCloner;
    }

    public void setStateEventCloner(StateEventCloner stateEventCloner) {
        this.stateEventCloner = stateEventCloner;
    }

    @Override
    public void resetState() {
        pendingStateEventList.clear();
        if (isStartState && newAndEveryStateEventList.isEmpty()) {
            //        if (isStartState && stateType == StateInputStream.Type.SEQUENCE && newAndEveryStateEventList.isEmpty()) {
            init();
        }
    }

    @Override
    public void updateState() {
        pendingStateEventList.addAll(newAndEveryStateEventList);
        newAndEveryStateEventList.clear();
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    @Override
    public int getStateId() {
        return stateId;
    }
}