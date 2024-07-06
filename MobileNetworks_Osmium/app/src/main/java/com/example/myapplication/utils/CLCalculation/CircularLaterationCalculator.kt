package com.example.myapplication.utils.CLCalculation

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.util.Pair

object CircularLaterationCalculator {
    fun lateration(positions: Array<DoubleArray>, distances: DoubleArray): DoubleArray {
        val numPoints = positions.size

        require(numPoints >= 3) { "please provide at least 3 points" }

        val A = Array(numPoints - 1) { DoubleArray(2) }
        val B = DoubleArray(numPoints - 1)

        for (i in 0 until numPoints - 1) {
            A[i][0] = 2 * (positions[i + 1][0] - positions[0][0])
            A[i][1] = 2 * (positions[i + 1][1] - positions[0][1])
            B[i] =
                (distances[0] * distances[0] - distances[i + 1] * distances[i + 1] - positions[0][0] * positions[0][0] + positions[i + 1][0] * positions[i + 1][0]
                        - positions[0][1] * positions[0][1] + positions[i + 1][1] * positions[i + 1][1])
        }

        val matrixA: RealMatrix = Array2DRowRealMatrix(A)
        val vectorB: RealVector = ArrayRealVector(B)
        val solver = LUDecomposition(matrixA).solver
        val solution = solver.solve(vectorB)

        return solution.toArray()
    }
    fun estimatePosition(positions: Array<DoubleArray>, distances: DoubleArray): DoubleArray {
        val n = positions.size

        val model = MultivariateJacobianFunction { point ->
            val values = DoubleArray(n)
            val jacobian = Array(n) { DoubleArray(2) }

            for (i in positions.indices) {
                val dx = point.toArray()[0] - positions[i][0]
                val dy = point.toArray()[1] - positions[i][1]
                values[i] = dx * dx + dy * dy - distances[i] * distances[i]
                jacobian[i][0] = 2 * dx
                jacobian[i][1] = 2 * dy
            }

            Pair(ArrayRealVector(values), Array2DRowRealMatrix(jacobian))
        }

        val target = ArrayRealVector(n)

        val problem = LeastSquaresBuilder()
            .start(doubleArrayOf(0.0, 0.0))
            .model(model)
            .target(target)
            .lazyEvaluation(false)
            .maxEvaluations(1000)
            .maxIterations(1000)
            .build()

        val optimum = org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer()
            .optimize(problem)
        return optimum.point.toArray()
    }

}

fun main() {

    val positions =
        arrayOf(
            doubleArrayOf(35.75195, 51.53178),
            doubleArrayOf(35.73276, 51.51766),
            doubleArrayOf(35.73753, 51.48329),
        )

    val distances = doubleArrayOf(
        1.0,
        1.0,
        1.0,
    )

    val estimatedPosition = CircularLaterationCalculator.lateration(positions, distances)
    println("Estimated Position: x = ${estimatedPosition[0]}, y = ${estimatedPosition[1]}")
}
