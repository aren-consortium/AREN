<template>
    <base-layout v-if="debate"
                 id="restitution">

        <template v-slot:title>
            <h1>Débat - {{ debate.document.name }} </h1>
            <label class="author">{{ $t("by").toLowerCase() }} <span>{{ debate.document.author }}</span> </label>
        </template>

        <template v-slot:right>
            <label class="details">
                <p>{{ $t('proposed_by') }} : {{ debate.owner.fullName() }}</p>
                <p>{{ $t('with') }} : {{ debate.teams.map( t => t.name ).join(", ") }}</p>
            </label>
        </template>


        <tabs-layout
            v-if="debate"
            v-bind:tabs="[
            {id: 'tree', label:$t('debate_tree')},
            {id: 'pie', label:$t('comment_repartition')},
            {id: 'sunburst', label:$t('debate_sunburst')},
            {id: 'tags', label:$t('tags'), hidden: !debate.idfixLink},
            {id: 'export', label:$t('odt_export'), href:'ws/debates/'+debate.id+'/export'},
            ]"
            v-bind:with-link="true">
            <template v-slot:tab.1>
                <d3-tree ref="debateTree"
                         v-bind:debate="debate"
                         @bullet="$router.push({ path: '../' + debate.id , query: { comment: $event.id } })">
                </d3-tree>
            </template>
            <template v-slot:tab.2>
                <d3-pie ref="debatePie"
                        v-bind:debate="debate">
                </d3-pie>
            </template>
            <template v-slot:tab.3>
                <d3-sunburst ref="debateSunburst"
                             v-bind:debate="debate">
                </d3-sunburst>
            </template>
            <template v-slot:tab.4>
                <tags-restitution ref="tagsRestitution"
                                  v-bind:debate="debate">
                </tags-restitution>
            </template>

            <template v-slot:tab.5>
                <div v-html="$t('helper.export', {debateId : debate.id})"></div>
            </template>
        </tabs-layout>

        <template v-slot:addons>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data() {
            return {
                debate: false,
                debateUsers: [],
                debateOpinions: [],
                byLetter: false,
                byOpinion: false,
                totalChar: 0,
                totalComment: 0
            }
        },
        mounted() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                ArenService.Debates.get({
                    id: this.$route.params.id,
                    onSuccess: (debate) => {
                        this.debate = debate;
                    },
                    onError: () => {
                        this.$router.push("/404");
                    }
                });
            }
        },
        components: {
            'd3-tree': vueLoader('components/widgets/D3Tree'),
            'd3-pie': vueLoader('components/widgets/D3Pie'),
            'd3-sunburst': vueLoader('components/widgets/D3Sunburst'),
            'tags-restitution': vueLoader('components/widgets/tagsRestitution'),
            'debate-print': vueLoader('components/widgets/debatePrint')
        }
    };
</script>