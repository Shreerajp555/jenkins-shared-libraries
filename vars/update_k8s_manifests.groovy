#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'jenkins@example.com'
    def gitBranch = config.gitBranch ?: 'master'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        // Configure Git
        sh '''
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        '''
        
        // Update deployment manifests with new image tags - using proper Linux sed syntax
        sh '''
            sed -i "s|image: shreerajp555/easyshop-app:.*|image: shreerajp555/easyshop-app:'"${imageTag}"'|g" kubernetes/08-easyshop-deployment.yaml

            if [ -f "kubernetes/12-migration-job.yaml" ]; then
                sed -i "s|image: shreerajp555/easyshop-migration:.*|image: shreerajp555/easyshop-migration:'"${imageTag}"'|g" kubernetes/12-migration-job.yaml
            fi

            if [ -f "kubernetes/10-ingress.yaml" ]; then
                sed -i "s|host: .*|host: easyshop.letsdeployit.com|g" kubernetes/10-ingress.yaml
            fi

            if [ -n "$(git status --porcelain)" ]; then
                git add kubernetes/*.yaml
                git commit -m "Update image tag to '"${imageTag}"' [ci skip]" || true
                git remote set-url origin https://$GIT_USERNAME:$GIT_PASSWORD@github.com/shreerajp555/tws-e-commerce-app.git
                git push origin HEAD:master || true
            else
                echo "No changes to commit"
            fi

            exit 0
        '''
    }
}
