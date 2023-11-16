LOCAL_PATH = os.getenv("LOCAL_PATH", default='.')
NAMESPACE = os.getenv("NAMESPACE", default='default')

k8s_custom_deploy(
    'spring-music',
    apply_cmd="tanzu apps workload apply -f config/workload.yaml --update-strategy replace --debug --live-update" +
              " --local-path " + LOCAL_PATH +
              " --namespace " + NAMESPACE +
              " --yes --output yaml",
    delete_cmd="tanzu apps workload delete -f config/workload.yaml --namespace " + NAMESPACE + " --yes",
    deps=['build.gradle', './build/libs'],
    container_selector='workload',
    live_update=[
      sync('./build/libs', '/workspace/BOOT-INF/classes')
    ]
)

k8s_resource('spring-music', port_forwards=["8080:8080"],
            extra_pod_selectors=[{'carto.run/workload-name': 'spring-music','app.kubernetes.io/component': 'run'}])
