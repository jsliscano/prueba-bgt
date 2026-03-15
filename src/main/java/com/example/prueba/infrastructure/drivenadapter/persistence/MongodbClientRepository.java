package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.port.ClientRepository;

import reactor.core.publisher.Mono;

@Component
public class MongodbClientRepository implements ClientRepository {

	private static final String SEQUENCE_COLLECTION = "sequences";
	private static final String CLIENT_SEQUENCE_ID = "client";

	private final SpringDataClientRepository repo;
	private final ReactiveMongoTemplate mongoTemplate;

	public MongodbClientRepository(SpringDataClientRepository repo, ReactiveMongoTemplate mongoTemplate) {
		this.repo = repo;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public Mono<Client> searchById(String id) {
		return repo.findById(id).map(ClientDocument::toDomain);
	}

	@Override
	public Mono<Client> saveClient(Client client) {
		return Mono.just(ClientDocument.from(client))
				.flatMap(repo::save)
				.map(ClientDocument::toDomain);
	}

	@Override
	public Mono<String> nextClientId() {
		Query query = new Query(Criteria.where("_id").is(CLIENT_SEQUENCE_ID));
		Update update = new Update().inc("value", 1);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
		return mongoTemplate.findAndModify(query, update, options, org.bson.Document.class, SEQUENCE_COLLECTION)
				.map(doc -> {
					Object v = doc.get("value");
					int num = v instanceof Number ? ((Number) v).intValue() : 1;
					return String.format("cliente-%03d", num);
				})
				.defaultIfEmpty("cliente-001");
	}
}
