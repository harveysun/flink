/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.api.functions;

import org.apache.flink.api.common.functions.InvalidTypesException;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.types.DataType;
import org.apache.flink.table.api.types.DataTypes;

/**
 * Base class for User-Defined Aggregates.
 *
 * <p>The behavior of an {@link AggregateFunction} can be defined by implementing a series of
 * custom methods. An {@link AggregateFunction} needs at least three methods:
 *  - createAccumulator,
 *  - accumulate, and
 *  - getValue.
 *
 * <p>There are a few other methods that can be optional to have:
 *  - retract,
 *  - merge, and
 *  - resetAccumulator
 *
 * <p>All these methods muse be declared publicly, not static and named exactly as the names
 * mentioned above. The methods createAccumulator and getValue are defined in the
 * {@link AggregateFunction} functions, while other methods are explained below.
 *
 *
 * {@code
 * Processes the input values and update the provided accumulator instance. The method
 * accumulate can be overloaded with different custom types and arguments. An AggregateFunction
 * requires at least one accumulate() method.
 *
 * param accumulator           the accumulator which contains the current aggregated results
 * param [user defined inputs] the input value (usually obtained from a new arrived data).
 *
 * public void accumulate(ACC accumulator, [user defined inputs])
 * }
 *
 *
 * {@code
 * Retracts the input values from the accumulator instance. The current design assumes the
 * inputs are the values that have been previously accumulated. The method retract can be
 * overloaded with different custom types and arguments. This function must be implemented for
 * datastream bounded over aggregate.
 *
 * param accumulator           the accumulator which contains the current aggregated results
 * param [user defined inputs] the input value (usually obtained from a new arrived data).
 *
 * public void retract(ACC accumulator, [user defined inputs])
 * }
 *
 *
 * {@code
 * Merges a group of accumulator instances into one accumulator instance. This function must be
 * implemented for datastream session window grouping aggregate and dataset grouping aggregate.
 *
 * param accumulator  the accumulator which will keep the merged aggregate results. It should
 *                     be noted that the accumulator may contain the previous aggregated
 *                     results. Therefore user should not replace or clean this instance in the
 *                     custom merge method.
 * param its          an {@link Iterable} pointed to a group of accumulators that will be
 *                     merged.
 *
 * public void merge(ACC accumulator, Iterable<ACC> its)
 * }
 *
 *
 * {@code
 * Resets the accumulator for this {@link AggregateFunction}. This function must be implemented for
 * dataset grouping aggregate.
 *
 * param accumulator  the accumulator which needs to be reset
 *
 * public void resetAccumulator(ACC accumulator)
 * }
 *
 * @param <T>   the type of the aggregation result
 * @param <ACC> the type of the aggregation accumulator. The accumulator is used to keep the
 *             aggregated values which are needed to compute an aggregation result.
 *             AggregateFunction represents its state using accumulator, thereby the state of the
 *             AggregateFunction must be put into the accumulator.
 */
public abstract class AggregateFunction<T, ACC> extends UserDefinedFunction {

	/**
	 * Creates and init the Accumulator for this {@link AggregateFunction}.
	 *
	 * @return the accumulator with the initial value
	 */
	public abstract ACC createAccumulator();

	/**
	 * Called every time when an aggregation result should be materialized.
	 * The returned value could be either an early and incomplete result
	 * (periodically emitted as data arrive) or the final result of the
	 * aggregation.
	 *
	 * @param accumulator the accumulator which contains the current
	 *                    aggregated results
	 * @return the aggregation result
	 */
	public abstract T getValue(ACC accumulator);

	/**
	 * Returns true if this AggregateFunction can only be applied in an OVER window.
	 *
	 * @return true if the AggregateFunction requires an OVER window, false otherwise.
	 */
	public boolean requiresOver() {
		return false;
	}

	/**
	 * Returns the DataType of the AggregateFunction's result.
	 *
	 * @return The DataType of the AggregateFunction's result or null if the result type
	 *         should be automatically inferred.
	 */
	public DataType getResultType() {
		return null;
	}

	/**
	 * Returns the DataType of the AggregateFunction's accumulator.
	 *
	 * @return The DataType of the AggregateFunction's accumulator or null if the
	 *         accumulator type should be automatically inferred.
	 */
	public DataType getAccumulatorType() {
		return null;
	}

	public DataType[] getUserDefinedInputTypes(Class[] signature) {
		DataType[] types = new DataType[signature.length];
		try {
			for (int i = 0; i < signature.length; i++) {
				types[i] = DataTypes.extractDataType(signature[i]);
			}
		} catch (InvalidTypesException e) {
			throw new ValidationException("Parameter types of scalar function '" +
					getClass().getCanonicalName() + "' cannot be automatically determined. " +
					"Please provide data type manually.");
		}
		return types;
	}

}
