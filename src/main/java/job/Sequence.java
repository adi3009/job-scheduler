package job;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sequence {
    public String order(String jobs) {
        if (jobs.isEmpty())
            return "";

        Graph<String> jobGraph = generateJobGraph(parseJobs(jobs));
        StringBuilder jobOrder = new StringBuilder();
        Stream<String> independentJobs = jobGraph.nodes().stream().filter(
                n -> jobGraph.successors(n).isEmpty() && jobGraph.predecessors(n).isEmpty()
        );

        independentJobs.forEach(jobOrder::append);

        Stream<String> hasDependents = jobGraph.nodes().stream().filter(
                n -> !jobGraph.successors(n).isEmpty() && jobGraph.predecessors(n).isEmpty()
        );

        Traverser<String> traverser = Traverser.forTree(jobGraph::successors);
        traverser.depthFirstPreOrder(hasDependents.collect(Collectors.toList())).forEach(jobOrder::append);

        return jobOrder.toString();
    }

    private Stream<Job> parseJobs(String jobs) {
        return Arrays.stream(jobs.split("\n")).map(Job::new);
    }

    private Graph<String> generateJobGraph(Stream<Job> jobs) {
        MutableGraph<String> graph = GraphBuilder.directed()
                .allowsSelfLoops(false)
                .build();

        jobs.forEach(job -> {
            if (job.isDependent())
                graph.putEdge(job.getDependsOn(), job.getName());
            else
                graph.addNode(job.getName());
        });

        return graph;
    }
}