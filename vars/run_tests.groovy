def call() {
    echo "Running unit tests..."
    sh '''
        npm install
        npm test
    '''
    // Add your unit test commands here
    // For example:
    // sh "npm test" or "mvn test" depending on your project
    
    echo "Unit tests completed successfully"
}
