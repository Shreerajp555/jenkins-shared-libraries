def call() {
    echo "Running unit tests..."

    sh """
        docker run --rm -v \$(pwd):/app -w /app node:18 \
        sh -c "npm install && npm test"
    """

    echo "Unit tests completed successfully"
}
