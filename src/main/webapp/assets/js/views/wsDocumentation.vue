<template>
    <base-layout id="documentation">
        <template v-slot:title>
            <h1>{{ $t('documentation') }}</h1>
        </template>

        <grid-layout
            v-bind:columns="[ '1fr', '1fr', '1fr', '1fr', '1fr', '1fr', '1fr' ]"
            v-bind:headers="[ $t('title'), $t('method'), $t('uri'), $t('parameters'), $t('return_value'), $t('authorisation'), $t('description') ]"
            v-bind:items="documentation"
            v-bind:grouped-items="( group ) => group.endpoints">

            <template v-slot:row.group="{ value: group }">
                {{ $t(group.name) }}
            </template>

            <template v-slot:column.1="{ value: endpoint }">
                {{ endpoint.method }}
            </template>

            <template v-slot:column.2="{ value: endpoint }">
                {{ endpoint.uri }}
            </template>

            <template v-slot:column.3="{ value: endpoint }">
                <div>
                    <template v-if="endpoint.pathParameters.length > 0">
                        {{ $t('path_parameters') }} :
                        <template v-for="param in endpoint.pathParameters">
                            {{ param.name }} ({{ param.type }})
                        </template>
                    </template>
                </div>
                <div>
                    <template v-if="endpoint.queryParameters.length > 0">
                        {{ $t('query_parameters') }} :
                        <template v-for="param in endpoint.queryParameters">
                            {{ param.type }}
                        </template>
                    </template>
                </div>

                <div>
                    <template v-if="endpoint.payloadParameters.length > 0">
                        {{ $t('playload_parameters') }} :
                        <template v-for="param in endpoint.payloadParameters">
                            {{ param.type }}
                        </template>
                    </template>
                </div>
            </template>

            <template v-slot:column.4="{ value: endpoint }">
                {{ endpoint.returnValue }}
            </template>

            <template v-slot:column.5="{ value: endpoint }">
                {{ endpoint.access }}
            </template>

            <template v-slot:column.6="{ value: endpoint }">
                {{ endpoint.name }}
            </template>

        </grid-layout>
    </base-layout>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                documentation: []
            };
        },
        created() {
            this.fetchData();
        },
        methods: {
            fetchData( ) {
                ArenService.Documentation({
                    onSuccess: (response) => {
                        this.documentation = response
                    }
                });
            }
        }
    };
</script>